package com.cs407.oyasumi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


//need to enable this alarm noticiation even if app is not running
//everytime notification is pushed code inside onRecieve will run
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("info", "AlarmReciever: going to show the alarm notification");
        AlarmHelper.getInstance().showAlarmNotification(context);
    }
}