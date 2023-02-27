package com.example.emotional_support_robot_app.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

@RequiresApi(api = Build.VERSION_CODES.R)
@SuppressLint("DefaultLocale")
public class ForegroundService extends Service {

    private static final double updateFrequency = 15; // in seconds
    private static final String NOTIFICATION_CHANNEL_ID = "example.permanence";
    private static final String channelName = "Background Service";

    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // create the custom or default notification
        // based on the android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());

    }

    @Override
    // Close overlay and foreground service if app is closed
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
    }

    /**
     * Create a notification (builder
     * @return The notification builder, which returns a notification
     */
    public NotificationCompat.Builder createNotification() {
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
    }

    // for android version >=O we need to create
    // custom notification stating
    // foreground service is running
    private void startMyOwnForeground()
    {
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_MIN);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        // create notification, which is always visible for the background service
        Notification notification = createNotification()
                .setOngoing(true)
                .setContentTitle(Html.fromHtml("<b>Emotional Support Robot App activated ✨</b>"))
                .setContentText("ESR is running quietly in the background and will listen for your wake word")

                // this is important, otherwise the notification will show the way
                // you want i.e. it will show some default notification
                //.setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                //.setColor(getResources().getColor(R.color.primaryVariant))
                .setColorized(true)

                .build();

        startForeground(2, notification);
    }
}
