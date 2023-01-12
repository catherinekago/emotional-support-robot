package com.example.emotional_support_robot_app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    private FirebaseFirestore firebase;
    private CollectionReference collectionRef;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
                    String sender = document.getDocument().getString("sender");
                    // If last entry was added by ROBOT, show loading screen, otherwise, show main screen
                    if (document.getType().equals(DocumentChange.Type.MODIFIED) || document.getType().equals(DocumentChange.Type.ADDED)){
                        if(sender.equals(getResources().getString(R.string.androidTag))) {
                            showLoadingScreen();
                        } else {
                            showMainScreen();
                        }
                    }
                    Log.d("E-S-R", "RETRIEVED LATEST ENTRY FROM: " + sender);
                } else {
                    showMainScreen();

                }
            }
        });

    }

    //region on click listeners for each button

    public void selectHappy(View view){
        pushToFirestore("HAPPY");
    }

    public void selectSad(View view){
        pushToFirestore("SAD");
    }

    public void selectAngry(View view){
        pushToFirestore("ANGRY");
    }

    public void selectAnxious(View view){
        pushToFirestore("ANXIOUS");
    }

    //endregion

    //region communication with firestore

    private void pushToFirestore(String emotionString){

        collectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // New request:
                            HashMap<String, String> message = new HashMap<String, String>();
                            HashMap<String, String> emotion = new HashMap<String, String>();
                            message.put("sender", getResources().getString(R.string.androidTag));
                            message.put("emotion", emotionString);
                            firebase.collection(getResources().getString(R.string.collectionPath)).document("MESSAGE").set(message);
                            showLoadingScreen();
                        } else {
                            Log.d("E-S-R", "Error getting documents: ", task.getException());
                        }
                    }
                });
    };

    //endregion

    //region adjust displayed content in activity

    private void showLoadingScreen(){

        // hide Title, buttonContainer1 and buttonContainer2
        this.findViewById(R.id.Title).setVisibility(View.INVISIBLE);
        this.findViewById(R.id.buttonContainer1).setVisibility(View.INVISIBLE);
        this.findViewById(R.id.buttonContainer2).setVisibility(View.INVISIBLE);

        // show LoadingMessage, Spinner
        this.findViewById(R.id.loadingContainer).setVisibility(View.VISIBLE);
    }

    private void showMainScreen(){

        // hide LoadingMessage, Spinner
        this.findViewById(R.id.loadingContainer).setVisibility(View.INVISIBLE);

        // show Title, buttonContainer1, buttonContainer2
        this.findViewById(R.id.Title).setVisibility(View.VISIBLE);
        this.findViewById(R.id.buttonContainer1).setVisibility(View.VISIBLE);
        this.findViewById(R.id.buttonContainer2).setVisibility(View.VISIBLE);
    }

    //endregion
}