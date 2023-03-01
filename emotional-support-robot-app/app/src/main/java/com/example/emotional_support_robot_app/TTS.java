package com.example.emotional_support_robot_app;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import java.util.HashMap;
import java.util.Locale;

public class TTS {

    public static TextToSpeech ttsObject;
    public static HashMap<String, String> ttsMap;

    public static void initializeTTS() {

        ttsMap = new HashMap<String, String>();
        ttsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");

        ttsObject = new TextToSpeech(Settings.mainActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS){
                    int result=ttsObject.setLanguage(Locale.ENGLISH);
                    ttsObject.setLanguage(Locale.US);


                    if (result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("TextToSpeech","Language Not Supported");
                    }

                    ttsObject.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {
                        }

                        @Override
                        public void onDone(String utteranceId) {
                                Settings.mainActivity.setListeningMode("request");
                        }

                        @Override
                        public void onError(String utteranceId) {
                            Log.i("E-S-R TTS","On Error");
                        }
                    });

                }else {
                    Log.i("E-S-R TTS","Initialization Failed");
                }
            }
        });
    }
};