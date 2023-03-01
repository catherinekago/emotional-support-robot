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
        startService();
        setupSpeechRecognizer();
        speechRecognizer.stopListening();
        
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

            @Override
            public void onResults(Bundle bundle) {
                Log.e("E-S-R", "on results");
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                utterance = (data.get(0));
                Log.e("E-S-R", utterance);
                isListening = false;
                errorText.setVisibility(View.INVISIBLE);
                handleUtterance(utterance);
            }

            @Override
            public void onReadyForSpeech(Bundle bundle) { Log.e("E-S-R", "ready for speech");
            }

            @Override
            public void onBeginningOfSpeech() { }

            @Override public void onRmsChanged(float v) { }

            @Override
            public void onBufferReceived(byte[] bytes) { }

            @Override
            public void onEndOfSpeech() {errorText.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onError(int i) {Log.e("E-S-R", "speech error " + i);
                isListening = true;
                errorText.setVisibility(View.VISIBLE);
                Log.e("E-S-R", "listening failed - start listening once again");
                speechRecognizer.cancel();
                setupSpeechRecognizer();
                speechRecognizer.startListening(speechRecognizerIntent);
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
    private void handleUtterance(String utterance) {

        utterance = utterance.toLowerCase(Locale.ROOT);

        if (Settings.status != StatusMessage.HAPPY) {
            determineEmotion(utterance);
        } else {
            determineSong(utterance);
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
            Settings.status = StatusMessage.HAPPY_109;
            MediaPlayer.playSong(this, R.raw.happy_109);
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
            title.setText("You selected \n \n" + Settings.status.name());
            speechRecognizer.cancel();
        } else if (hasSexy || hasKnow) {
            Settings.status = StatusMessage.HAPPY_128;
            MediaPlayer.playSong(this, R.raw.happy_128);
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
            title.setText("You selected \n \n" + Settings.status.name());
            speechRecognizer.cancel();

        } else {
            errorText.setVisibility(View.VISIBLE);
            isListening = true;
            speechRecognizer.cancel();
            setupSpeechRecognizer();
            speechRecognizer.startListening(speechRecognizerIntent);
        }

    }

    /**
     * Analyze the user utterance to identify if it includes keywords of the emotions the user can
     * choose from and handle the corresponding case accordingly.
     * @param utterance The utterance returned from the speech recognition
     */
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
            Settings.status = StatusMessage.ANXIOUS;
            title.setText("You selected \n \n" + Settings.status.name());
            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
            speechRecognizer.cancel();
        } else if (hasHappy || hasAmazing || hasAwesome || hasGood || hasGreat || hasDancing) {
            Settings.status = StatusMessage.HAPPY;
            provideSongOptions();
        } else {
            errorText.setVisibility(View.VISIBLE);
            isListening = true;
            speechRecognizer.cancel();
            setupSpeechRecognizer();
            speechRecognizer.startListening(speechRecognizerIntent);
        }
    }

    /**
     * Display the song options and start the speech recognizer so the user can choose a song.
     */
    private void provideSongOptions() {
        // show title options, start listening
        title.setText("What dance music would you like? \n \n WALKING ON SUNSHINE \n or \n SEXY AND I KNOW IT");
        isListening = true;
        speechRecognizer.cancel();
        setupSpeechRecognizer();
        speechRecognizer.startListening(speechRecognizerIntent);
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
                                Settings.porcupineManager.start();
                                break;

                            case WAKEWORD:
                                try {
                                    Settings.porcupineManager.stop();
                                    title.setText("Hi! Waking up...");
                                } catch (PorcupineException porcupineException) {
                                    porcupineException.printStackTrace();
                                }
                                break;

                            case AWAKE:
                                title.setText("How do you feel? \n \n ANXIOUS, HAPPY?");
                                    if (!isListening) {
                                        isListening = true;
                                        speechRecognizer.cancel();
                                        setupSpeechRecognizer();
                                        speechRecognizer.startListening(speechRecognizerIntent);
                                    }
                                break;

                            case PLAYING:
                                    speechRecognizer.cancel();

                                title.setText("ESRA in action ...");
                                break;

                            case STOP:
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


}