package com.cs407.oyasumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static AlarmManager alarmManager = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", -1);
        int minute = sharedPreferences.getInt("minute", -1);
        if((hour == -1)||(minute == -1))
            Log.i("Info", "MainActivity: alarm not set");
        else {
            Log.i("Info", "MainActivity: alarm set");
            setAlarm();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.alarmClockMenuItem) {
            //use an intent to go to the alarm activity
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
            return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setAlarm() {
        Log.i("info", "MainActivity: setting alarm");
        SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", -1);
        int minute = sharedPreferences.getInt("minute", -1);
        Log.i("info", "MainActivity: hour to be set - " + hour);
        Log.i("info", "MainActivity: minute to be set - " + minute);

        AlarmHelper.getInstance().createNotificationChannel(this);

        AlarmHelper.getInstance().setCalendar(hour, minute);
        Log.i("info", "MainActivity: setting alarm for: "  + AlarmHelper.getInstance().getTime());

        Intent intentForReceiver = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, intentForReceiver, PendingIntent.FLAG_IMMUTABLE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancelAll();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP,AlarmHelper.getInstance().getTime(), pendingIntent);
        if(alarmManager.canScheduleExactAlarms())
            Log.i("info", "MainActivity: can schedule exact alarms");
        else
            Log.i("info", "MainActivity: this is the problem");
        Log.i("info", "MainActivity: alarm successfully saved");
    }
}