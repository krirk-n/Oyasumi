package com.cs407.oyasumi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class AlarmHelper {

    private static final String CHANNEL_ID = "OYASUMI_ALARM";

    public static final AlarmHelper INSTANCE = new AlarmHelper();

    private static final Calendar calendar = Calendar.getInstance();

    private static final int ARGB = 1217141249;


    private AlarmHelper() {}

    public static AlarmHelper getInstance() {
        return INSTANCE;
    }

    public void createNotificationChannel(Context context) {
        //check that the current operating system version is correct in order to create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.oyasumi_alarm_channel_name);
            String description = context.getString(R.string.oyasumi_alarm_channel_description);

            //int importance = NotificationManager.IMPORTANCE_DEFAULT; //notif goes directly into drawer without popping up
            int importance = NotificationManager.IMPORTANCE_HIGH; //changed this for debugging purposes
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            //register the NotificationChannel object with the android system
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void setCalendar(int hour, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    public long getTime() {
        return calendar.getTimeInMillis();
    }

    public void showAlarmNotification(Context context) {
        //if we do not have permission to show notifications, we cannot execute this function
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.i("info", "AlarmHelper: notification permissions not granted, cannot show alarm");
            return;
        }
        //TODO potentially create pending intent here to open new activity when user taps on notification
        //TODO if so, need to uncomment line 77

        //create the notification
        @SuppressLint("RemoteViewLayout") RemoteViews customAlarmView = new RemoteViews(context.getPackageName(), R.layout.alarm_notification_layout);
       // Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ context.getApplicationContext().getPackageName() + "/" + R.raw.mysound);
        Log.i("info", "AlarmHelper: building the alarm notification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                //.setContentTitle("oyasumi alarm notification")
                //.setContentText("time to wake up")
                .setContent(customAlarmView)
                //.setAutoCancel(true) //when user clicks on notification, it can be canceled
                .setDefaults(Notification.DEFAULT_ALL)
                .setLights(ARGB, 10, 10)
               // .setSound() //TODO can set a stream as well
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        //.setContentIntent(pendingIntent);

        //use a notification manager to build the created notification
        NotificationManagerCompat notificationmanagerCompat = NotificationManagerCompat.from(context);
        notificationmanagerCompat.notify(123, builder.build());
    }
}
