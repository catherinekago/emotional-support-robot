package com.example.emotional_support_robot_app;

import android.app.Activity;

public class MediaPlayer {

    public static android.media.MediaPlayer mediaPlayer;
    public static int length = 0;

    public static void playSong(Activity activity, int songReference) {
        mediaPlayer = android.media.MediaPlayer.create(activity, songReference);
        mediaPlayer.start();
        length = 0;
    }

    public static void stopSong() {
        mediaPlayer.stop();
        mediaPlayer.release();
        length = 0;
    }

    public static void pauseSong(){
        mediaPlayer.pause();
        length=mediaPlayer.getCurrentPosition();
    }

    public static void resumeSong(){
        mediaPlayer.seekTo(length);
        mediaPlayer.start();
        length = 0;
    }

}
