package com.example.emotional_support_robot_app.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.emotional_support_robot_app.FirestoreHandler;
import com.example.emotional_support_robot_app.MainActivity;
import com.example.emotional_support_robot_app.R;
import com.example.emotional_support_robot_app.Settings;
import com.example.emotional_support_robot_app.StatusMessage;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import ai.picovoice.porcupine.Porcupine;
import ai.picovoice.porcupine.PorcupineException;
import ai.picovoice.porcupine.PorcupineManager;

@RequiresApi(api = Build.VERSION_CODES.R)
@SuppressLint("DefaultLocale")
public class ForegroundService extends Service {

    private FirebaseFirestore firebase;
    private CollectionReference collectionRef;

    private static final double updateFrequency = 15; // in seconds
    private static final String NOTIFICATION_CHANNEL_ID = "example.permanence";
    private static final String channelName = "Background Service";

    private String accessKey = "XtZfmmOD3T09VbD3Y7A/sV8B/gdKarnZQMSWK2YFSlDWIAljYsZNHA=="; // your Picovoice AccessKey
    private PorcupineManager porcupineManager;

    public ForegroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // setup firebase connection
        this.firebase = FirebaseFirestore.getInstance();
        this.collectionRef = firebase.collection(getResources().getString(R.string.collectionPath));

        try {
            // Add own wake word
            porcupineManager = new PorcupineManager.Builder()
                    .setAccessKey(accessKey)
                    .setSensitivity(0.7f)
                    .setKeywordPaths(new String[]{"Hey-Ezra_en_android_v2_1_0.ppn"})
                    .build(getApplicationContext(),
                            (keywordIndex) -> {
                                // wake word detected!

                                if(Settings.status == StatusMessage.SNAKE){
                                    Settings.status = StatusMessage.WAKEWORD;
                                    Log.e("E-S-R", "HEY ESRA");
                                    // post "WAKEWORD" to database
                                    FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
                                    try {
                                        TimeUnit.SECONDS.sleep(3);
                                        if (Settings.status != StatusMessage.AWAKE){
                                            Settings.status = StatusMessage.SNAKE;
                                            Log.e("E-S-R", "NO RESPONSE");
                                            FirestoreHandler.pushToFirestore(this, firebase, collectionRef, Settings.status.name());
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                }

                            });
            porcupineManager.start();
        } catch (PorcupineException e) {
            Log.e("E-S-R PORCUPINE SERVICE", e.toString());
        }
        return super.onStartCommand(intent, flags, startId);
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
