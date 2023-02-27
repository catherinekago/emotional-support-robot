package com.example.emotional_support_robot_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class HappyActivity extends AppCompatActivity {

    private FirebaseFirestore firebase;
    private CollectionReference collectionRef;

    private TextView title;
    private WebView youtubeWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_happy);

        // setup firebase connection
        this.firebase = FirebaseFirestore.getInstance();
        this.collectionRef = firebase.collection(getResources().getString(R.string.collectionPath));

        title = findViewById(R.id.Title);
        title.setText(getResources().getString(R.string.happy_default));

        findViewById(R.id.buttonContainer1).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonContainer2).setVisibility(View.VISIBLE);
        findViewById(R.id.playSongContainer).setVisibility(View.GONE);
    }

    //TODO: exchange 3-4 with bpm of song
    public void select1(View view) {
        MediaPlayer.playSong(this, R.raw.happy_109);
        // push happy + bpm to firestore
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "HAPPY_109");
        finish();
    }

    public void select2(View view) {
        MediaPlayer.playSong(this, R.raw.happy_128);
        // push happy + bpm to firestore
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "HAPPY_128");
        finish();
    }

    public void select3(View view) {
        MediaPlayer.playSong(this, R.raw.happy_109);
        // push happy + bpm to firestore
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "HAPPY_TODO");
        finish();
    }

    public void select4(View view) {
        MediaPlayer.playSong(this, R.raw.happy_109);
        // push happy + bpm to firestore
        FirestoreHandler.pushToFirestore(this, firebase, collectionRef, "HAPPY_TODO");
        finish();
    }

}