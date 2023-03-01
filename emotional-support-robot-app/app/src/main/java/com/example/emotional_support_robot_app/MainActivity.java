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

    private boolean isListening;
    private String utterance;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
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
        isListening = false;

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                utterance = (data.get(0));
                Log.e("E-S-R UTTERANCE", utterance);
                isListening = false;
                setListeningMode("mute");
                handleUtterance(utterance);
            }

            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.d("E-S-R SPEECH", "ready");
            }

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
                isListening = true;
                if(Settings.status != StatusMessage.SNAKE) {
                    //Log.d("E-S-R SPEECH", "error  " + i + "  -- try request again");
                    setListeningMode("request");
                } else {
                    setListeningMode("activation");
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.d("E-S-R SPEECH EVENT", String.valueOf(i));
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
            TTS.ttsObject.speak(getResources().getString(R.string.STOP), TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
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
        setListeningMode("mute");

        if (utterance.contains("walking") || utterance.contains("sunshine")){
            Settings.song = R.raw.happy_109;
            communicateInput(StatusMessage.HAPPY_109);
            TTS.ttsObject.speak(getResources().getString(R.string.PLAYING_HAPPY_109), TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
        } else if (utterance.contains("sexy") || utterance.contains("know")) {
            Settings.song = R.raw.happy_128;
            communicateInput(StatusMessage.HAPPY_128);
            TTS.ttsObject.speak(getResources().getString(R.string.PLAYING_HAPPY_128), TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
        } else {
            handleNoMatch();
        }

    }

    /**
     * Handle case where speech recognition does not return any matches
     */
    private void handleNoMatch() {
        isListening = true;
        setListeningMode("request");

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
            TTS.ttsObject.speak(getResources().getString(R.string.PLAYING_ANXIOUS), TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
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
        setListeningMode("mute");
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
        TTS.ttsObject.speak(getResources().getString(R.string.SONGS), TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
        //setListeningMode("request");
        isListening = true;
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
                        Log.d("E-S-R", "MESSAGE -- " + messageBody);
                        Settings.status =StatusMessage.valueOf(messageBody);

                        switch (Settings.status){
                            case SNAKE:
                                if(MediaPlayer.mediaPlayer != null){
                                    MediaPlayer.mediaPlayer.release();
                                }
                                setListeningMode("activation");
                                break;

                            case WAKEWORD:
                                setListeningMode("request");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                }

                            case AWAKE:
                                if (!isListening) {
                                    isListening = true;
                                    setListeningMode("request");
                                }
                                break;

                            case PLAYING:

                                if (Settings.song != 0){
                                    MediaPlayer.playSong(Settings.mainActivity, Settings.song);
                                    // Reset song variable
                                    Settings.song = 0;
                                }
                                setListeningMode("request");
                                break;


                            case STOP:
                                // Stop music if playing
                                if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                                    MediaPlayer.stopSong();
                                }
                                try {
                                    Thread.sleep(500);
                                } catch (InterruptedException interruptedException) { interruptedException.printStackTrace();}

                                TTS.ttsObject.speak(getResources().getString(R.string.STOP), TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
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
    public void setListeningMode(String mode) {
        // set app to listen for activation word
        if (mode.equals("activation")) {
            Settings.porcupineManager.start();
            Log.d("E-S-R", "hey esra active");
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
            //Log.d("E-S-R", "speech recognition active");
        } else if (mode.equals("mute")){
            try {
                Settings.porcupineManager.stop();
            } catch (PorcupineException porcupineException) {
                porcupineException.printStackTrace();
            }
            speechRecognizer.stopListening();
            speechRecognizer.cancel();
        }
    }


}