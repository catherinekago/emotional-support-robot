package com.example.emotional_support_robot_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.emotional_support_robot_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private TextView loadingMessage;
    private Button stopButton;
    private FirebaseFirestore firebase;
    private CollectionReference collectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.loadingMessage = findViewById(R.id.LoadingMessage);
        loadingMessage.setText(getString(R.string.loading_playing));
        this.stopButton = findViewById(R.id.buttonStop);

        setUpFirestore();


    }

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
                Log.d("E-S-R", "FIREBASE EVENT");

                QuerySnapshot snapshots = (QuerySnapshot) object;

                if(snapshots.getDocumentChanges().size() != 0){
                    DocumentChange document = snapshots.getDocumentChanges().get(0);
                    String tag = document.getDocument().getId().split("_")[0];
                    String messageBody = document.getDocument().getString("body");
                    if (document.getType().equals(DocumentChange.Type.MODIFIED) || document.getType().equals(DocumentChange.Type.ADDED)){
                        Boolean isReady = messageBody.equals(getResources().getString(R.string.body_ready));
                        Boolean isHappy = messageBody.equals("HAPPY");
                        // show waiting screen for all emotions and state except for ready (robot idle mode) and happy (where app displays specific content)
                        if(!(isReady || isHappy)) {
                            showLoadingScreen(messageBody);
                        } else {
                            showMainScreen();
                        }
                    }
                    Log.d("E-S-R", "MESSAGE FROM: " + document.getDocument().getString("sender") + " -- " + messageBody);
                } else {
                    showMainScreen();

                }
            }
        });
    }

    //region on click listeners for each button

    @SuppressLint("ResourceAsColor")
    public void selectHappy(View view){
        Intent switchActivityIntent = new Intent(this, HappyActivity.class);
        startActivity(switchActivityIntent);
    }

    @SuppressLint("ResourceAsColor")
    public void selectSad(View view){
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "SAD");
        showLoadingScreen(getResources().getString(R.string.body_playing));
    }

    @SuppressLint("ResourceAsColor")
    public void selectAngry(View view){
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "ANGRY");
        findViewById(R.id.Spinner).setBackgroundColor(R.color.angry);
        showLoadingScreen(getResources().getString(R.string.body_playing));
    }

    @SuppressLint({"ResourceAsColor", "ResourceType"})
    public void selectAnxious(View view){
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "ANXIOUS");
        showLoadingScreen(getResources().getString(R.string.body_playing));
    }

    // Click listener for stop button

    public void stop(View view){
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "STOP");
        showLoadingScreen(getResources().getString(R.string.body_stop));

        if (MediaPlayer.mediaPlayer.isPlaying()){
            MediaPlayer.stopSong();
        }
    }

    //endregion

    //region adjust displayed content in activity

    @SuppressLint("ResourceType")
    void showLoadingScreen(String state) {

        // hide Title, buttonContainer1 and buttonContainer2
        this.findViewById(R.id.Title).setVisibility(View.INVISIBLE);
        this.findViewById(R.id.buttonContainer1).setVisibility(View.INVISIBLE);
        this.findViewById(R.id.buttonContainer2).setVisibility(View.INVISIBLE);

        // show LoadingMessage, Spinner
        this.findViewById(R.id.loadingContainer).setVisibility(View.VISIBLE);

        if (state.equals(getResources().getString(R.string.body_stop))) {
            loadingMessage.setText(getString(R.string.loading_stop));
            this.findViewById(R.id.Spinner).setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.INVISIBLE);
        } else {
            loadingMessage.setText(getString(R.string.loading_playing));
            this.findViewById(R.id.Spinner).setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);

        }
    }

    void showMainScreen(){

        // hide LoadingMessage, Spinner
        this.findViewById(R.id.loadingContainer).setVisibility(View.INVISIBLE);

        // show Title, buttonContainer1, buttonContainer2
        this.findViewById(R.id.Title).setVisibility(View.VISIBLE);
        this.findViewById(R.id.buttonContainer1).setVisibility(View.VISIBLE);
        this.findViewById(R.id.buttonContainer2).setVisibility(View.VISIBLE);
    }

    //endregion

}