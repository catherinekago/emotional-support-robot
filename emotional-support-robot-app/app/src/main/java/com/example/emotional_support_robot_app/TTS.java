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
        ttsObject = new TextToSpeech(Settings.mainActivity.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status==TextToSpeech.SUCCESS){
                    int result=ttsObject.setLanguage(Locale.ENGLISH);

                    if (result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.i("TextToSpeech","Language Not Supported");
                    }

                    ttsObject.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onStart(String utteranceId) {

                        }

                        @Override
                        public void onDone(String utteranceId) {
                            Boolean statusIsAwake = Settings.status == StatusMessage.AWAKE;
                            Boolean statusIsHappy = Settings.status == StatusMessage.HAPPY;
                            Boolean statusIsWakeword = Settings.status == StatusMessage.WAKEWORD;
                            if (!(statusIsAwake|| statusIsHappy || statusIsWakeword)){
                                Settings.mainActivity.setListeningMode("activation");
                                Log.d("E-S-R TTS","On Done - start ACTIVATION LISTENING again");
                            } else {
                                Settings.mainActivity.setListeningMode("request");
                                Log.d("E-S-R TTS","On Done - start REQUEST again");
                            }
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

        ttsObject.setLanguage(Locale.US);
        ttsMap = new HashMap<String, String>();
        ttsMap.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
    }
};