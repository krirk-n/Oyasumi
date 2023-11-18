package com.cs407.oyasumi;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

public class AlarmActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String> requestNotificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if(!isGranted) {
                    //if the permission is refused, send a toast
                    Toast.makeText(this, R.string.please_allow_alarm_notification, Toast.LENGTH_LONG).show();
                }
            });

    private void requestNotificationPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            //notification permission not required till Android 13 (Tiramasu)
            return;
        }
        //we check if the permission is granted already and if not, ask for permission using the
        //static launcher
        if(ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {
            //permission not granted
            requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        requestNotificationPermission();
    }

    public void saveAlarm(View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.alarmClock);
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("hour", hour).apply();
        sharedPreferences.edit().putInt("minute", minute).apply();

        Toast.makeText(this, "Alarm Saved", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void cancelAlarm(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", -1);
        int minute = sharedPreferences.getInt("minute", -1);
        if ((hour == -1) || (minute == -1))
            openDialog();
        else {
            Toast.makeText(this, "Using Previous Alarm Setting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void openDialog() {
        NoAlarmAlertDialog dialog = new NoAlarmAlertDialog();
        dialog.show(getSupportFragmentManager(), "no alarm set dialog");
    }
}