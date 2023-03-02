package com.example.emotional_support_robot_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.emotional_support_robot_app.MediaPlayer.*;
import static com.example.emotional_support_robot_app.MediaPlayer.playFeedbackSound;

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
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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

    private static final int SLEEP_LONG = 5500;
    private static final int SLEEP_MEDIUM = 3500;
    private static final int SLEEP_SHORT = 3000;

    private TextView title;

    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.title = findViewById(R.id.Title);

        ConstraintLayout mainLayout = findViewById(R.id.mainContainer);
        mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(Global.status == StatusMessage.PLAYING) {
                    handleSuccessfulInput(StatusMessage.STOP);
                } }

        });

        setUpFirestore();
        Global.status = StatusMessage.SNAKE;
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Global.status.name());

        Global.mainActivity = this;

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
                //Log.d("E-S-R SPEECH", "started speaking");
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
     * @param utterance the utterance coming from voice recognition
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void handleUtterance(String utterance) {
        utterance = utterance.toLowerCase(Locale.ROOT);

        // determine emotion for wakeword / awake
        if (Global.status == StatusMessage.WAKEWORD || Global.status == StatusMessage.AWAKE) {
            determineEmotion();

            // determine song for happy state
        } else if (Global.status == StatusMessage.HAPPY) {
            determineSong(utterance);
            // determine cycle length for anxious state
        } else if (Global.status == StatusMessage.ANXIOUS) {
            determineCycleLength(utterance);

            // ask user if they want to continue with the breathing exercise
        } else if (Global.status == StatusMessage.ANXIOUS_END){
            determineContinuation(utterance);
        }
        else if (Global.status == StatusMessage.PLAYING){
            listenForStop(utterance);
        }

    }

    /**
     * Analyze utterance for next action after completed anti-anxiety sequence
     * @param utterance the utterance that was provided via speech input
     */
    private void determineContinuation(String utterance) {
        setListeningMode("mute", false);
        if (utterance.contains("yes") || utterance.contains("continue")) {
            performTTS(getResources().getString(R.string.PLAYING_ANXIOUS_CONTINUE), SLEEP_SHORT,false);
            handleSuccessfulInput(StatusMessage.ANXIOUS_SHORT);
        } else if (utterance.contains("no") || utterance.contains("stop") || utterance.contains("done") || utterance.contains("good") ){
            handleSuccessfulInput(StatusMessage.STOP);
        } else {
            handleNoMatch();
        }
    }

    /**
     * Analyze utterance for cycle length for box breathing
     * @param utterance the utterance that was provided via speech input
     */
    private void determineCycleLength(String utterance) {
        setListeningMode("mute", false);

        if (utterance.contains("short")) {
            handleSuccessfulInput(StatusMessage.ANXIOUS_SHORT);
            performTTS(getResources().getString(R.string.PLAYING_ANXIOUS), SLEEP_SHORT, false);
        } else if (utterance.contains("medium")) {
            handleSuccessfulInput(StatusMessage.ANXIOUS_MEDIUM);
            performTTS(getResources().getString(R.string.PLAYING_ANXIOUS), SLEEP_SHORT, false);
        } else if (utterance.contains("long")) {
            handleSuccessfulInput(StatusMessage.ANXIOUS_LONG);
            performTTS(getResources().getString(R.string.PLAYING_ANXIOUS), SLEEP_SHORT, false);
        }  else {
            handleNoMatch();
        }
    }

    /**
     * Identify stop word for stopping the robot action
     * @param utterance the utterance returned from the speech recognition
     */
    private void listenForStop(String utterance) {
        if (utterance.contains("stop")){
            handleSuccessfulInput(StatusMessage.STOP);
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
            Global.song = R.raw.happy_244;
            handleSuccessfulInput(StatusMessage.HAPPY_244);
            performTTS(getResources().getString(R.string.PLAYING_HAPPY_244), SLEEP_SHORT, false);
        } else if (utterance.contains("sexy") || utterance.contains("know")) {
            Global.song = R.raw.happy_208;
            handleSuccessfulInput(StatusMessage.HAPPY_208);
            performTTS(getResources().getString(R.string.PLAYING_HAPPY_208), SLEEP_SHORT, false);
        } else {
            handleNoMatch();
        }

    }

    /**
     * Perform text to speech conversion and provide a timeout before restarting speech recognition
     * @param text the text to be spoken
     */
    public void performTTS(String text, int sleepTime, Boolean prompt) {
        Log.d("E-S-R", "start tts");
        TTS.ttsObject.speak(text, TextToSpeech.QUEUE_FLUSH, TTS.ttsMap);
        sleep(sleepTime);
        Log.d("E-S-R", "continue listening");
        if (prompt) {
            playFeedbackSound(R.raw.success);
        }
        setListeningMode("request", true);

    }

    /**
     * Handle case where speech recognition does not return any matches
     */
    private void handleNoMatch() {
       if(Global.status != StatusMessage.PLAYING){
            performTTS("Sorry, would you mind repeating that?", SLEEP_SHORT, true);
      } else {
           Log.d("E-S-R", "no match, but don't want to speak during song!");
       }

    }

    /**
     * Analyze the user utterance to identify if it includes keywords of the emotions the user can
     * choose from and handle the corresponding case accordingly.
     */
    @SuppressLint("ResourceAsColor")
    private void determineEmotion() {
        // ANXIETY KEYWORDS
        String[] anxietyKeywords = new String [] {"anxious", "anxiety", "nervous", "breathe", "breathing"};
        Boolean hasKeyword = checkForKeywords(anxietyKeywords);

        if (hasKeyword){
            handleSuccessfulInput(StatusMessage.ANXIOUS);
            provideBoxBreathingCycleOptions();

        } else {
            // HAPPY KEYWORDS
            String[] happyKeywords = new String [] {"happy", "amazing", "awesome", "good", "great", "puppy", "copy", "hubby"};
            hasKeyword = checkForKeywords(happyKeywords);

            if (hasKeyword) {
                handleSuccessfulInput(StatusMessage.HAPPY);
                provideSongOptions();
            } else {
                handleNoMatch();
            }
        }
    }

    /**
     * Check array for keywords
     * @param keyWords array of keywords
     * @return true or false, depending on if at least one keyword has been detected
     */
    private Boolean checkForKeywords(String[] keyWords) {
        for (String keyWord : keyWords) {
            if (utterance.contains(keyWord)) {
                return true;
            }
        }
        return false;
    }

    private void provideBoxBreathingCycleOptions() {
        performTTS(getResources().getString(R.string.BOX_BREATHING_CYCLES), SLEEP_LONG, true);
    }

    /**
     * When valid input has been identified, provide feedback on success and communicate new status to db
     * @param status the identified status
     */
    private void handleSuccessfulInput(StatusMessage status) {
        playFeedbackSound(R.raw.success);

        Global.status = status;
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Global.status.name());
        setListeningMode("mute", false);
        sleep(500);
    }

    /**
     * Sleep for specified amount of time
     * @param milliseconds how long thread is put to sleep
     */
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    /**
     * Display the song options and start the speech recognizer so the user can choose a song.
     */
    private void provideSongOptions() {
        // provide options, start listening
        performTTS(getResources().getString(R.string.SONGS),  SLEEP_LONG, true);
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

            @SuppressLint("ResourceAsColor")
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
                        Log.d("E-S-R", "FIREBASE -- received " + messageBody);
                        Global.status =StatusMessage.valueOf(messageBody);

                        switch (Global.status){
                            case SNAKE:
                                title.setText(R.string.title_snake);
                                title.setTypeface(null, Typeface.NORMAL);
                                title.setTextColor(getResources().getColor(R.color.grey));
                                releaseMediaPlayer();
                                setListeningMode("activation", false);
                                break;

                            case WAKEWORD:
                                title.setTypeface(null, Typeface.BOLD);
                                title.setTextColor(getResources().getColor(R.color.teal_400));
                                break;

                            case AWAKE:
                                title.setText("");
                                sleep(1500);
                                performTTS(getResources().getString(R.string.AWAKE), SLEEP_MEDIUM, true);
                                break;

                            case PLAYING:
                                title.setTextColor(getResources().getColor(R.color.grey));
                                title.setTypeface(null, Typeface.NORMAL);
                                title.setText(R.string.title_stop);
                                if (Global.song != 0){
                                    playSong(Global.mainActivity, Global.song);
                                    // Reset song variable
                                    Global.song = 0;
                                }
                                setListeningMode("request", false);
                                break;

                            case ANXIOUS_END:
                                askForAnotherBreathingCycle();
                                break;

                            case STOP:
                                title.setTypeface(null, Typeface.BOLD);
                                title.setTextColor(getResources().getColor(R.color.teal_400));
                                stopSong();
                                performTTS(getResources().getString(R.string.STOP), SLEEP_SHORT,  false);
                                break;
                        }
                    }
                }
            }
        });
    }

    
    /**
     * If the breathing cycle is completed, ask the user if they want to continue
     */
    private void askForAnotherBreathingCycle() {
        setListeningMode("mute", false);
        performTTS("Hey there. Do you want to continue breathing?", SLEEP_SHORT, true);

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
                Global.porcupineManager.start();
                speechRecognizer.stopListening();
                speechRecognizer.cancel();


                // set app to listen for spoken requests
            } else if (mode.equals("request")) {
                try {
                    Global.porcupineManager.stop();
                } catch (PorcupineException porcupineException) {
                    porcupineException.printStackTrace();
                }
                setupSpeechRecognizer();
                speechRecognizer.startListening(speechRecognizerIntent);
            } else if (mode.equals("mute")){
                try {
                    Global.porcupineManager.stop();
                } catch (PorcupineException porcupineException) {
                    porcupineException.printStackTrace();
                }
                speechRecognizer.stopListening();
                speechRecognizer.cancel();
            }
            LISTEN_MODE = mode;
        }
    }


}