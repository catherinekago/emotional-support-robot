package com.example.emotional_support_robot_app;

import android.app.Activity;

public class MediaPlayer {

    public static android.media.MediaPlayer mediaPlayer;

    public static void playSong(Activity activity, int songReference) {
        mediaPlayer = android.media.MediaPlayer.create(activity, songReference);
        mediaPlayer.start();
    }

    static void stopSong() {
       if(MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()) {
           mediaPlayer.stop();
           mediaPlayer.release();
       }
    }

    static void releaseMediaPlayer(){
        if(MediaPlayer.mediaPlayer != null){
            MediaPlayer.mediaPlayer.release();
        }
    }

    /**
     * Provide feedback for successful input recognition
     */
    static void provideSuccessFeedback() {
        // Stop music if playing
        MediaPlayer.releaseMediaPlayer();
        MediaPlayer.playSong(Global.mainActivity, R.raw.ping);
    }

}
