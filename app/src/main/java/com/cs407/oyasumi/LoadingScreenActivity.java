package com.cs407.oyasumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoadingScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        ExampleRunnable runnable = new ExampleRunnable();
        new Thread(runnable).start();

    }

    public void mockLoadingScreen() {
        Log.i("Info", "In the new thread");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.loadingBar);

        if(progressBar == null) {
            Log.i("Info", "LoadingActivity: progressBar view is null");
            return;
        }

        TextView loadingText = (TextView) findViewById(R.id.loadingText);

        for(int loadingProgress = 0; loadingProgress <= 100; loadingProgress += 10) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Toast.makeText(this, "Error Loading App, Please Close and Try Again", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            progressBar.setProgress(loadingProgress);
            //YoYo.with(Techniques.Tada).duration(700).playOn(findViewById(R.id.loadingText));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    class ExampleRunnable implements Runnable {
        @Override
        public void run() {
            mockLoadingScreen();
        }
    }
}