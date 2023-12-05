package com.cs407.oyasumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class SleepingActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorEventListener sensorEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleeping);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float z = event.values[2];
                adjustBrightness(z);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getY() - e2.getY() > 50 && Math.abs(velocityY) > 100) {
                    Log.d("Check", "swipe detected!");
                    navigateToMainPage();
                    return true;
                }
                return false;
            }
        });

        // Set up the listener for the whole view or a specific view
        View myView = findViewById(R.id.relativeLayout);
        myView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void adjustBrightness(float z) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        if (z > 0) {
            // Phone facing upwards, increase brightness
            layoutParams.screenBrightness = 1.0f;
        } else {
            // Phone facing downwards, decrease brightness
            layoutParams.screenBrightness = 0.01f;
        }
        getWindow().setAttributes(layoutParams);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void navigateToMainPage() {
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = getSharedPreferences("startTime", Context.MODE_PRIVATE);
        Log.d("SharedPref","startSecond: " + sharedPreferences.getLong("startSecond", -1));
        Log.d("SharedPref","startDate: " + sharedPreferences.getString("startDate", "no"));
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Note> notes = dbHelper.readNotes();
        int notesSize = notes.size();
        int noteId = notesSize + 1;
        long currentSecond = System.currentTimeMillis() / 1000;
        int sleepDuration = (int)(currentSecond - sharedPreferences.getLong("startSecond", -1));
        sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        dbHelper.saveNote(noteId, sharedPreferences.getString("startDate", "something wrong"), sleepDuration,
                "", "","");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}