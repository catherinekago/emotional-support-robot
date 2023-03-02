package com.example.emotional_support_robot_app;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * The FirestoreHandler class handled updating the value stored on firebase
 * on user input.
 */
public class FirestoreHandler {

    public static void pushToFirestore(Context context, FirebaseFirestore firebase, CollectionReference collectionRef, String body){

        collectionRef
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        HashMap<String, String> message = new HashMap<>();
                        message.put("body", body);
                        firebase.collection(context.getResources().getString(R.string.collectionPath)).document("MESSAGE").set(message);
                    } else {
                        Log.d("E-S-R", "Error getting documents: ", task.getException());
                    }
                });
    }

    //endregion
}
