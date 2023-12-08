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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

        getSupportActionBar().hide();

        //reorder the views
        sendViewToBack(findViewById(R.id.alarm_cloud1));
        sendViewToBack(findViewById(R.id.alarm_cloud2));
        sendViewToBack(findViewById(R.id.alarm_star1));
        sendViewToBack(findViewById(R.id.alarm_star2));
        sendViewToBack(findViewById(R.id.alarmTimeBackground));
        sendViewToBack(findViewById(R.id.alarm_star3));

        requestNotificationPermission();
    }

    public void saveAlarm(View view) {
        //check if the appropriate alarm notifications have been enabled in order to use
        if ((ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED)||(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.USE_FULL_SCREEN_INTENT) !=
                PackageManager.PERMISSION_GRANTED)) {
            openNoPermissionSetDialog();
        } else {

            //get the desired time from the time picker
            TimePicker timePicker = (TimePicker) findViewById(R.id.alarmClock);
            int hour = timePicker.getHour();
            int minute = timePicker.getMinute();

            //save the time in shared preferences
            SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("hour", hour).apply();
            sharedPreferences.edit().putInt("minute", minute).apply();
            Log.i("info", "MainActivity: hour to be set - " + hour);
            Log.i("info", "MainActivity: minute to be set - " + minute);


            AlarmHelper.getInstance().createNotificationChannel(this);

            AlarmHelper.getInstance().setCalendar(hour, minute);
            Log.i("info", "AlarmActivity: setting alarm for: " + AlarmHelper.getInstance().getTime());

            //set up the alarm notification using alarm manager
            Intent intentForReceiver = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intentForReceiver, PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancelAll(); //clear any previously set alarms before doing a new one TODO must be changed if want more robust alarm in future
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmHelper.getInstance().getTime(), pendingIntent);
            Log.i("info", "AlarmActivity: alarm successfully saved");

            //send toast to user to notify them of saved changes
            Toast.makeText(this, "Alarm Saved", Toast.LENGTH_SHORT).show();

            //go back to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void cancelAlarm(View view) {
        //get the previously set time from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", -1);
        int minute = sharedPreferences.getInt("minute", -1);

        //if there is no previously set time, send notification to user about not being able to use alarm
        if ((hour == -1) || (minute == -1))
            openNoTimeSetDialog();
        //otherwise just send toast to user notifying them that previous settings are loaded and go back to MainActivity
        else {
            Toast.makeText(this, "Using Previous Alarm Setting", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void openNoTimeSetDialog() {
        NoTimeSetDialog dialog = new NoTimeSetDialog();
        dialog.show(getSupportFragmentManager(), "no alarm set dialog");
    }

    public void openNoPermissionSetDialog() {
        NoPermissionSetDialog dialog = new NoPermissionSetDialog();
        dialog.show(getSupportFragmentManager(), "no permissions enabled dialog");
    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }
}