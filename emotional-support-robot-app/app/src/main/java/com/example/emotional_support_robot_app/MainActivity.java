package com.example.emotional_support_robot_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.emotional_support_robot_app.service.ForegroundService;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Locale;

import ai.picovoice.porcupine.PorcupineException;


public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebase;
    private CollectionReference collectionRef;

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private String utterance;

    private long lastErrorTimeout = System.currentTimeMillis();
    private static final long ERROR_TIMEOUT_LENGTH = 1000;

    private static String LISTEN_MODE;

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id.mainContainer);
        mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Settings.status == StatusMessage.PLAYING) {
                    communicateInput(StatusMessage.STOP);
                } }

        });

        setUpFirestore();
        Settings.status = StatusMessage.SNAKE;
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());

        Settings.mainActivity = this;

        //Setup speech recognizer
        setupSpeechRecognizer();
        speechRecognizer.cancel();

        // Setup tts object
        TTS.initializeTTS();

        startService();

        // Check permissions
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            checkPermission();
        }
    }

    /**
     * Setup speech recognition object
     */
    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                utterance = (data.get(0));
                Log.e("E-S-R UTTERANCE", utterance);
                handleUtterance(utterance);
            }

            @Override
            public void onReadyForSpeech(Bundle bundle) { }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("E-S-R SPEECH", "started speaking");
            }

            @Override public void onRmsChanged(float v) { }

            @Override
            public void onBufferReceived(byte[] bytes) { }

            @Override
            public void onEndOfSpeech() {
            }

            @Override
            public void onError(int i) {
                long currentTime = System.currentTimeMillis();
                if(lastErrorTimeout + ERROR_TIMEOUT_LENGTH < currentTime){
                    lastErrorTimeout = currentTime;
                    setListeningMode(LISTEN_MODE, true);
                    }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                //Log.d("E-S-R SPEECH EVENT", String.valueOf(i));
            }
        });
    }

    /**
     * Analyze the user utterance depending on the current status. For "HAPPY", continue to song
     * selection, while for any other state (which should be AWAKE), determine the emotion.
     * @param utterance
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void handleUtterance(String utterance) {
        utterance = utterance.toLowerCase(Locale.ROOT);

        // determine emotion for wakeword / awake
        if (Settings.status == StatusMessage.WAKEWORD || Settings.status == StatusMessage.AWAKE) {
            determineEmotion(utterance);

            // determine song for happy state
        } else if (Settings.status == StatusMessage.HAPPY) {
            determineSong(utterance);
        } else if (Settings.status == StatusMessage.PLAYING){
            listenForStop(utterance);
        }

    }

    /**
     * Identify stop word for stopping the robot action
     * @param utterance the utterance returned from the speech recognition
     */
    private void listenForStop(String utterance) {
        if (utterance.contains("stop")){
            communicateInput(StatusMessage.STOP);
            performTTS(getResources().getString(R.string.STOP));
        } else {
            handleNoMatch();
        }
    }

    /**
     * Analyze the user utterance to identify if it includes keywords of the songs the user can
     * choose from and select the matching song accordingly.
     * @param utterance The utterance returned from the speech recognition
     */
    private void determineSong(String utterance) {
        setListeningMode("mute", false);

        if (utterance.contains("walking") || utterance.contains("sunshine")){
            Settings.song = R.raw.happy_244;
            communicateInput(StatusMessage.HAPPY_244);
            performTTS(getResources().getString(R.string.PLAYING_HAPPY_244));
        } else if (utterance.contains("sexy") || utterance.contains("know")) {
            Settings.song = R.raw.happy_208;
            communicateInput(StatusMessage.HAPPY_208);
            performTTS(getResources().getString(R.string.PLAYING_HAPPY_208));
        } else {
            handleNoMatch();
        }

    }

    public void performTTS(String text) {
        Log.d("E-S-R", "start tts");
        TTS.ttsObject.speak(text, TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("E-S-R", "continue listening");
        setListeningMode("request", false);
    }

    /**
     * Handle case where speech recognition does not return any matches
     */
    private void handleNoMatch() {
        setListeningMode("request", true);

    }

    /**
     * Analyze the user utterance to identify if it includes keywords of the emotions the user can
     * choose from and handle the corresponding case accordingly.
     * @param utterance The utterance returned from the speech recognition
     */
    @SuppressLint("ResourceAsColor")
    private void determineEmotion(String utterance) {
        // ANXIETY KEYWORDS
        String[] anxietyKeywords = new String [] {"anxious", "anxiety", "nervous", "breathe", "breathing"};
        Boolean hasAnxietyKeywords = false;
        for (int i = 0; i < anxietyKeywords.length; i++){
            if (utterance.contains(anxietyKeywords[i])){
                hasAnxietyKeywords = true;
                break;
            }
        }
        if (hasAnxietyKeywords){
            communicateInput(StatusMessage.ANXIOUS);
            performTTS(getResources().getString(R.string.PLAYING_ANXIOUS));
        } else {

            String[] happyKeywords = new String [] {"happy", "amazing", "awesome", "good", "great", "puppy", "copy", "hubby"};
            Boolean hasHappyKeywords = false;
            for (int i = 0; i < happyKeywords.length; i++){
                if (utterance.contains(happyKeywords[i])){
                    hasHappyKeywords = true;
                    break;
                }
            }
            if (hasHappyKeywords) {
                communicateInput(StatusMessage.HAPPY);
                provideSongOptions();
            } else {
                handleNoMatch();
            }
        }
    }

    /**
     * When valid input has been identified, provide feedback on success and communicate new status to db
     * @param status the identified status
     */
    private void communicateInput(StatusMessage status) {
        // Stop music if playing
        if (MediaPlayer.mediaPlayer != null){
            MediaPlayer.mediaPlayer.release();
        }
        MediaPlayer.playSong(Settings.mainActivity, R.raw.ping);
        Settings.status = status;
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
        setListeningMode("mute", false);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Display the song options and start the speech recognizer so the user can choose a song.
     */
    private void provideSongOptions() {
        // provide options, start listening
        performTTS(getResources().getString(R.string.SONGS));
    }

    /**
     * Check for record audio permissions for speech recognition.
     */
    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    // method for starting the service
    @RequiresApi(api = Build.VERSION_CODES.R)
    public void startService() {
        startService(new Intent(this, ForegroundService.class));
    }

    /**
     * Setup the firestore listener to handle each incoming status accordingly.
     */
    private void setUpFirestore() {
        // setup firebase connection
        this.firebase = FirebaseFirestore.getInstance();
        this.collectionRef = firebase.collection(getResources().getString(R.string.collectionPath));

        //setup firebase event listener
        collectionRef.addSnapshotListener(new EventListener() {

            @Override
            public void onEvent(@Nullable Object object, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }

                QuerySnapshot snapshots = (QuerySnapshot) object;

                if(snapshots.getDocumentChanges().size() != 0){
                    DocumentChange document = snapshots.getDocumentChanges().get(0);
                    String tag = document.getDocument().getId().split("_")[0];
                    String messageBody = document.getDocument().getString("body");
                    if (document.getType().equals(DocumentChange.Type.MODIFIED) || document.getType().equals(DocumentChange.Type.ADDED)){
                        //Log.d("E-S-R", "MESSAGE -- " + messageBody);
                        Settings.status =StatusMessage.valueOf(messageBody);

                        switch (Settings.status){
                            case SNAKE:
                                if(MediaPlayer.mediaPlayer != null){
                                    MediaPlayer.mediaPlayer.release();
                                }
                                setListeningMode("activation", false);
                                break;

                            case WAKEWORD:
                                setListeningMode("request", false);
                                Log.d("E-S-R", "wakeword");

                            case AWAKE:
                                    setListeningMode("request", false);
                                break;

                            case PLAYING:

                                if (Settings.song != 0){
                                    MediaPlayer.playSong(Settings.mainActivity, Settings.song);
                                    // Reset song variable
                                    Settings.song = 0;
                                }
                                setListeningMode("request", false);
                                break;


                            case STOP:
                                // Stop music if playing
                                if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                                    MediaPlayer.stopSong();
                                }
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException interruptedException) { interruptedException.printStackTrace();}
                                performTTS(getResources().getString(R.string.STOP));
                        }
                    }
                }
            }
        });
    }

    /**
     * Select mode for listening for wake word or user's spoken request
     * @param mode the mode to be selected: choose either activation or request
     */
    public void setListeningMode(String mode, Boolean error) {
        // set mode if it is null, if mode changed, or if error occurred
        if (LISTEN_MODE == null || !LISTEN_MODE.equals(mode) || error){

            Log.d("E-S-R SpeechRec", "ERROR " + error +" -- " + LISTEN_MODE);

            // set app to listen for activation word
            if (mode.equals("activation")) {
                Settings.porcupineManager.start();
                speechRecognizer.stopListening();
                speechRecognizer.cancel();


                // set app to listen for spoken requests
            } else if (mode.equals("request")) {
                try {
                    Settings.porcupineManager.stop();
                } catch (PorcupineException porcupineException) {
                    porcupineException.printStackTrace();
                }
                setupSpeechRecognizer();
                speechRecognizer.startListening(speechRecognizerIntent);
            } else if (mode.equals("mute")){
                try {
                    Settings.porcupineManager.stop();
                } catch (PorcupineException porcupineException) {
                    porcupineException.printStackTrace();
                }
                speechRecognizer.stopListening();
                speechRecognizer.cancel();
            }
            LISTEN_MODE = mode;
        } else {
            Log.d("E-S-R SpeechRec", "DENIED - MODE ALREADY ACTIVE");
        }

    }


}