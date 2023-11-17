package com.cs407.oyasumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPreferences = getSharedPreferences("alarm_data", Context.MODE_PRIVATE);
        long hour = sharedPreferences.getLong("hour", -1);
        long minute = sharedPreferences.getLong("minute", -1);
        if((hour == -1)||(minute == -1))
            Log.i("Info", "alarm not set");
        else {
            Log.i("Info", "alarm set");
            startAlarm();
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

    private void startAlarm() {
        //TODO will be implemented to start alarm service
        //AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }
}