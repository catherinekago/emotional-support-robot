package com.example.emotional_support_robot_app;

import android.app.Activity;

public class MediaPlayer {

    public static android.media.MediaPlayer mediaPlayer;

    public static void playSong(Activity activity, int songReference) {
        mediaPlayer = android.media.MediaPlayer.create(activity, songReference);
        mediaPlayer.start();
    }

    public static void stopSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

}
