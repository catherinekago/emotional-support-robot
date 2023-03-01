package com.example.emotional_support_robot_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emotional_support_robot_app.R;
import com.example.emotional_support_robot_app.service.ForegroundService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private TextView title;
    private TextView errorText;

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

        this.title = findViewById(R.id.Title);
        this.errorText = findViewById(R.id.errorText);

        setUpFirestore();
        Settings.status = StatusMessage.SNAKE;
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());

        Settings.mainActivity = this;

        startService();
        setupSpeechRecognizer();
        speechRecognizer.cancel();
        
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
                Log.e("E-S-R SPEECH", utterance);
                isListening = false;
                errorText.setVisibility(View.INVISIBLE);
                handleUtterance(utterance);
            }

            @Override
            public void onReadyForSpeech(Bundle bundle) { Log.d("E-S-R SPEECH", "ready");
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onBeginningOfSpeech() {
            }

            @Override public void onRmsChanged(float v) { }

            @Override
            public void onBufferReceived(byte[] bytes) { }

            @Override
            public void onEndOfSpeech() {errorText.setVisibility(View.INVISIBLE);

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onError(int i) {
                isListening = true;
                errorText.setVisibility(View.VISIBLE);
                //Log.d("E-S-R SPEECH", "error  " + i + "  -- try again");
                if(Settings.status == StatusMessage.AWAKE || Settings.status == StatusMessage.HAPPY) {
                    setListeningMode("request");
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) { }

            @Override
            public void onEvent(int i, Bundle bundle) { }
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

            // determine action during pause
        }  else if (Settings.status == StatusMessage.PAUSE) {
            determineNextAction(utterance);
        }

    }

    /**
     * Determine next action depending on utterance
     * @param utterance
     */
    @RequiresApi(api = Build.VERSION_CODES.R)
    private void determineNextAction(String utterance) {
        // RESUME
        Boolean hasResume = utterance.contains("resume");
        Boolean hasContinue = utterance.contains("continue");

        // STOP
        Boolean hasStop = utterance.contains("stop");
        Boolean hasBye = utterance.contains("by");
        Boolean hasGoodNight = utterance.contains("good night");
        Boolean hasSleep = utterance.contains("sleep");

        if (hasResume || hasContinue){
            Settings.status = StatusMessage.RESUME;
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());

        } else if (hasStop || hasBye || hasGoodNight || hasSleep) {
            Settings.status = StatusMessage.STOP;
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
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
        // WALKNG ON SUNSHINE
        Boolean hasWalking = utterance.contains("walking");
        Boolean hasSunshine = utterance.contains("sunshine");

        // SEXY AND I KNOW IT
        Boolean hasSexy = utterance.contains("sexy");
        Boolean hasKnow = utterance.contains("know");


        if (hasWalking || hasSunshine){
            // Stop music if playing
            if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                MediaPlayer.stopSong();
            }
            MediaPlayer.playSong(Settings.mainActivity, R.raw.ping);

            Settings.status = StatusMessage.HAPPY_109;
            Settings.song = R.raw.happy_109;
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
            title.setText("You selected \n \n" + Settings.status.name());
        } else if (hasSexy || hasKnow) {
            // Stop music if playing
            if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                MediaPlayer.stopSong();
            }
            MediaPlayer.playSong(Settings.mainActivity, R.raw.ping);
            Settings.status = StatusMessage.HAPPY_128;
            Settings.song = R.raw.happy_128;
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
            title.setText("You selected \n \n" + Settings.status.name());
        } else {
            handleNoMatch();
        }

    }

    /**
     * Handle case where speech recognition does not return any matches
     */
    private void handleNoMatch() {
        errorText.setVisibility(View.VISIBLE);
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
        Boolean hasAnxious = utterance.contains("anxious");
        Boolean hasAnxiety = utterance.contains("anxiety");
        Boolean hasNervous = utterance.contains("nervous");
        Boolean hasBreathe = utterance.contains("breathe");
        Boolean hasBreathing = utterance.contains("breathing");

        // HAPPY KEYWORDS
        Boolean hasHappy = utterance.contains("happy");
        Boolean hasAmazing = utterance.contains("amazing");
        Boolean hasAwesome = utterance.contains("awesome");
        Boolean hasGood = utterance.contains("good");
        Boolean hasGreat = utterance.contains("great");
        Boolean hasDancing = utterance.contains("dancing");

        if (hasAnxiety || hasAnxious || hasNervous || hasBreathe || hasBreathing){
            // Stop music if playing
            if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                MediaPlayer.stopSong();
            }
            MediaPlayer.playSong(Settings.mainActivity, R.raw.ping);
            Settings.status = StatusMessage.ANXIOUS;
            title.setText("You selected \n \n" + Settings.status.name());
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());

        } else if (hasHappy || hasAmazing || hasAwesome || hasGood || hasGreat || hasDancing) {
            // Stop music if playing
            if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                MediaPlayer.stopSong();
            }
            MediaPlayer.playSong(Settings.mainActivity, R.raw.ping);
            Settings.status = StatusMessage.HAPPY;
            provideSongOptions();
        } else {
            handleNoMatch();
        }
    }

    /**
     * Display the song options and start the speech recognizer so the user can choose a song.
     */
    private void provideSongOptions() {
        // show title options, start listening
        title.setText("What dance music would you like? \n \n WALKING ON SUNSHINE \n or \n SEXY AND I KNOW IT");
        setListeningMode("request");
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
                                title.setText("Say \n \n HEY ESRA");
                                setListeningMode("activation");

                                // Stop music if playing
                               //if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                                 //   MediaPlayer.stopSong();
                               // }
                                break;

                            case WAKEWORD:
                                setListeningMode("request");
                                title.setText("Hi! Waking up...");
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException interruptedException) {
                                    interruptedException.printStackTrace();
                                }

                            case AWAKE:
                                title.setText("How do you feel? \n \n ANXIOUS, HAPPY?");
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

                                title.setText("ESRA in action ...");
                                setListeningMode("activation");
                                break;

                            case PAUSE:
                                title.setText("I am PAUSING");
                                // Stop music if playing
                                if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                                    MediaPlayer.pauseSong();
                                }
                                setListeningMode("request");


                                break;

                            case RESUME:
                                title.setText("I am RESUMING");

                                // Start music if it was paused
                                if (MediaPlayer.mediaPlayer != null && MediaPlayer.length != 0){
                                    MediaPlayer.resumeSong();
                                }
                                break;

                            case STOP:
                                title.setText("I am STOPPING");
                                // Stop music if playing
                                if (MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()){
                                    MediaPlayer.stopSong();
                                }
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
    private void setListeningMode(String mode) {
        // set app to listen for activation word
        if (mode.equals("activation")) {
            Settings.porcupineManager.start();
            Log.d("E-S-R", "hey esra active");
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
        }
    }


}