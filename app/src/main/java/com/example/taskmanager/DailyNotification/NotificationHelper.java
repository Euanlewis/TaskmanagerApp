package com.example.taskmanager.DailyNotification;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;


import androidx.core.app.NotificationCompat;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;

public class NotificationHelper {

    private Context xContext;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    NotificationHelper(Context context){
        xContext = context;
    }

    void createNotification(){

        Intent intent = new Intent(xContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(xContext, 200, intent, FLAG_IMMUTABLE);

        NotificationCompat.Builder xBuilder = new NotificationCompat.Builder(xContext, NOTIFICATION_CHANNEL_ID);
        xBuilder.setSmallIcon(R.mipmap.ic_launcher);
        xBuilder.setContentTitle("TaskMana Reminder")
                .setContentText("Prepare your tasks for tomorrow!")
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager xNotificationManager = (NotificationManager) xContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            assert xNotificationManager != null;
            xBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            xNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert xNotificationManager != null;
        xNotificationManager.notify(200, xBuilder.build());
    }
}
