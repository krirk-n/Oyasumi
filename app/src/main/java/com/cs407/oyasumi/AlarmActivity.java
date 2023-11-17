package com.cs407.oyasumi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
    }


    public void cancelAlarm(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("alarm_data", Context.MODE_PRIVATE);
        long hour = sharedPreferences.getLong("hour", -1);
        long minute = sharedPreferences.getLong("minute", -1);
        if ((hour == -1) || (minute == -1))
            openDialog();
        else {
            Toast.makeText(this, "Using Previous Alarm Setting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void saveAlarm(View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.alarmClock);
        long hour = timePicker.getHour();
        long minute = timePicker.getMinute();
        SharedPreferences sharedPreferences = getSharedPreferences("alarm_data", Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong("hour", hour).apply();
        sharedPreferences.edit().putLong("minute", minute).apply();
        Intent intent = new Intent(this, MainActivity.class);
        Toast.makeText(this, "Alarm Saved", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }


    public void openDialog() {
        NoAlarmAlertDialog dialog = new NoAlarmAlertDialog();
        dialog.show(getSupportFragmentManager(), "no alarm set dialog");
    }
}