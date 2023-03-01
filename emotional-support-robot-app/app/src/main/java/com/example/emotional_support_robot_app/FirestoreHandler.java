package com.example.emotional_support_robot_app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class FirestoreHandler {

    public static void pushToFirestore(Context context, FirebaseFirestore firebase, CollectionReference collectionRef, String body){

        collectionRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // New request:
                            HashMap<String, String> message = new HashMap<String, String>();
                            message.put("body", body);
                            firebase.collection(context.getResources().getString(R.string.collectionPath)).document("MESSAGE").set(message);
                            Log.d("E-S-R   SEND",  body);
                        } else {
                            Log.d("E-S-R", "Error getting documents: ", task.getException());
                        }
                    }
                });
    };

    //endregion
}
