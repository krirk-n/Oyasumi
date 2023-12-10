package com.cs407.oyasumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmDisplayActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_display);

        SharedPreferences sharedPreferences = getSharedPreferences("oyasumi_alarm_data", Context.MODE_PRIVATE);
        int hour = sharedPreferences.getInt("hour", -1);
        int minute = sharedPreferences.getInt("minute", -1);

        //logic to convert the time to 24 hour clock
        String ampm = "";
        if (hour >= 12)
            ampm = "PM";
        else
            ampm = "AM";

        if(hour > 12)
            hour -= 12;
        String minuteString = "";
        if(minute < 10)
            minuteString = "0" + Integer.toString(minute);
        else
            minuteString = Integer.toString(minute);

        //set the time text

        TextView time = (TextView) findViewById(R.id.wakeUpTime);
        time.setText(hour + ":" + minuteString + " " + ampm);

        //reorder the views
        sendViewToBack(findViewById(R.id.wakeUpBackgroundImage));
        sendViewToBack(findViewById(R.id.wakeUpCloud1));
    }

    public void dismissAlarm(View view) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        // Mute the notification stream
        audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, 0, 0);
        //go to sleep activity
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public static void sendViewToBack(final View child) {
        final ViewGroup parent = (ViewGroup)child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }
}