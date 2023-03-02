package com.example.emotional_support_robot_app;

import android.annotation.SuppressLint;

import ai.picovoice.porcupine.PorcupineManager;

/**
 * The Global class holds all objects that need to be accessible from different classes.
 */
public class Global {

    // The current status that is communicated over firebase
    public static StatusMessage status;

    // The wake word manager object
    public static PorcupineManager porcupineManager;

    // The reference to the song
    public static int song = 0;

    // The main activity of the app
    @SuppressLint("StaticFieldLeak")
    public static MainActivity mainActivity;
}
