package com.example.emotional_support_robot_app;

import android.app.Activity;

/**
 * THe MediaPlayer class holds the MediaPlayer object and provides the functions to play and stop
 * songs and to release resources.
 */
public class MediaPlayer {

    public static android.media.MediaPlayer mediaPlayer;

    /**
     * Play song
     * @param activity the main activity that calls the MediaPlayer
     * @param songReference the song to be played
     */
    public static void playSong(Activity activity, int songReference) {
        mediaPlayer = android.media.MediaPlayer.create(activity, songReference);
        mediaPlayer.start();

    }

    /**
     * Stop and release MediaPlayer
     */
    static void stopSong() {
       if(MediaPlayer.mediaPlayer != null && MediaPlayer.mediaPlayer.isPlaying()) {
           mediaPlayer.stop();
           releaseMediaPlayer();
       }
    }

    /**
     * Release MediaPlayer
     */
    static void releaseMediaPlayer(){
        if(MediaPlayer.mediaPlayer != null){
            MediaPlayer.mediaPlayer.release();
        }
    }

    /**
     * Play sound feedback
     */
    static void playFeedbackSound(int soundFile) {
        // Stop music if playing
        MediaPlayer.releaseMediaPlayer();
        MediaPlayer.playSong(Global.mainActivity, soundFile);
    }

}
