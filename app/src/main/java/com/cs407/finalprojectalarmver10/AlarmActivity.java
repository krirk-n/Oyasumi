package com.cs407.finalprojectalarmver10;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.Time;

public class AlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
    }


    public void cancelAlarm(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("alarm_data", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", -1);
        int minute = sharedPreferences.getInt("minute", -1);
        if((hour == -1)||(minute == -1))
            openDialog();
        else {
            Toast.makeText(this, "Using Previous Alarm Setting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void saveAlarm(View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.alarmClock);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();
        SharedPreferences sharedPreferences = getSharedPreferences("alarm_data", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("hour", hour);
        sharedPreferences.edit().putInt("minute", minute);
        sharedPreferences.edit().apply();
        Intent intent = new Intent(this, MainActivity.class);
        Toast.makeText(this, "Alarm Saved", Toast.LENGTH_SHORT).show();
        startActivity(intent);

    }

    public void openDialog() {
        NoAlarmAlertDialog dialog = new NoAlarmAlertDialog();
        dialog.show(getSupportFragmentManager(), "no alarm set dialog");
    }
}