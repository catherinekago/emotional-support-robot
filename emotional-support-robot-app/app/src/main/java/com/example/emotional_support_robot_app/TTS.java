package com.example.emotional_support_robot_app;

import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

/**
 * The TTS class holds the TextToSpeech object and a function to initialize the object to enable
 * text-to-speech conversion.
 */
public class TTS {

    public static TextToSpeech ttsObject;
    public static HashMap<String, String> ttsMap = new HashMap<String, String>();

    public static void initializeTTS() {

        ttsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        ttsObject = new TextToSpeech(Global.mainActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS) ttsObject.setLanguage(Locale.US);
                else Log.i("E-S-R TTS", "Initialization Failed");
            }
        });
    }
};