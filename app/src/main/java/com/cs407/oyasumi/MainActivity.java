package com.cs407.oyasumi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView dateTextView;
    TextView sleepDurationTextView;
    TextView sleepQualityTextView;
    TextView interpretTextView;
    EditText dreamEditText;
    EditText additionEditText;
    FloatingActionButton prevFloatingActionButton;
    FloatingActionButton nextFloatingActionButton;
    Button sleepButton;
    Button interpretButton;
    Note currentNote;
    private int currentPage = -1;
    private int notesSize = 0;
    private String stringURLEndPoint = "https://api.openai.com/v1/chat/completions";
    private String stringAPIKey = "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";
    private String stringOutput = "";

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
        }

        Context context = getApplicationContext();
        createDummyNotes(3);

        prevFloatingActionButton = (FloatingActionButton) findViewById(R.id.prevFloatingActionButton);
        nextFloatingActionButton = (FloatingActionButton) findViewById(R.id.nextFloatingActionButton);
        sleepButton = (Button) findViewById(R.id.sleepButton);
        interpretButton = (Button) findViewById(R.id.interpretButton);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        sleepDurationTextView = (TextView) findViewById(R.id.sleepDurationTextView);
        sleepQualityTextView = (TextView) findViewById(R.id.sleepQualityTextView);
        dreamEditText = (EditText) findViewById(R.id.dreamEditText);
        additionEditText = (EditText) findViewById(R.id.additionEditText);
        interpretTextView = (TextView) findViewById(R.id.interpretTextView);

        prevFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentPage == -1) {
                    currentPage = notesSize-1;
                } else {
                    currentPage--;
                    saveDreamAndAdditionalNote();
                }
                resetUI();
                Log.i("currentPage", String.valueOf(currentPage));
                checkButtons();
            }
        });
        nextFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                if (currentPage >= notesSize) {
                    currentPage = -1;
                    loadEmptyPage();
                } else {
                    saveDreamAndAdditionalNote();
                    resetUI();
                }
                Log.i("currentPage", String.valueOf(currentPage));
                checkButtons();
            }
        });

        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Note> notes = dbHelper.readNotes();
        notesSize = notes.size();

        // Empty page for upcoming sleep
        checkButtons();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText(currentDate);
        sleepDurationTextView.setText("");
        sleepQualityTextView.setText("");

    }

    public void checkButtons() {
        prevFloatingActionButton.setEnabled(currentPage > 0 || currentPage == -1);
        nextFloatingActionButton.setEnabled(currentPage != -1);
        if (currentPage == -1) {
            sleepButton.setVisibility(View.VISIBLE);
            dreamEditText.setVisibility(View.GONE);
            interpretTextView.setVisibility(View.GONE);
            interpretButton.setVisibility(View.GONE);
            additionEditText.setVisibility(View.GONE);
        }
        else {
            sleepButton.setVisibility(View.GONE);
            dreamEditText.setVisibility(View.VISIBLE);
            interpretTextView.setVisibility(View.VISIBLE);
            interpretButton.setVisibility(View.VISIBLE);
            additionEditText.setVisibility(View.VISIBLE);
        }
    }

    public void resetUI() {
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Note> notes = dbHelper.readNotes();
        currentNote = notes.get(currentPage); // show the recent note
        dateTextView.setText(currentNote.getDate());
        sleepDurationTextView.setText("I've slept for " + currentNote.getSleepDuration() / 3600 + " hours "
                + (currentNote.getSleepDuration() % 3600) / 60 + " minutes."); // assume we get seconds from sleep duration
        sleepQualityTextView.setText("My sleep quality was " + currentNote.getSleepQuality() + ".");
        dreamEditText.setText(currentNote.getDreamNote());
        interpretTextView.setText(currentNote.getDreamInterpret());
        additionEditText.setText(currentNote.getAdditionalNote());
    }

    public void loadEmptyPage() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        dateTextView.setText(currentDate);
        sleepDurationTextView.setText("");
        sleepQualityTextView.setText("");
        dreamEditText.setText("");
        interpretTextView.setText("");
        additionEditText.setText("");
    }

    public void saveDreamAndAdditionalNote() {
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        String newAdditionalNote = additionEditText.getText().toString();
        String newDreamNote = dreamEditText.getText().toString();
        String newInterpret = interpretTextView.getText().toString();
        Log.i("add", "new: " + newInterpret);
        dbHelper.updateNote(currentNote.getNoteId(), currentNote.getDate(), currentNote.getSleepDuration(),
                currentNote.getSleepQuality(), newDreamNote, newInterpret, newAdditionalNote);
    }

    public void createDummyNotes(int num) {
        ArrayList<String> sleepQualityTypes = new ArrayList<>(Arrays.asList("poor", "fine", "excellent")) ;
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        for (int i=1; i <= num; i++ ) {
            DBHelper dbHelper = new DBHelper(sqLiteDatabase);
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String date = dateFormat.format(new Date());
            ArrayList<Note> notes = dbHelper.readNotes();
            int notesSize = notes.size();
            int noteId = notesSize + 1;
            Random random = new Random();
            String sleepQuality = sleepQualityTypes.get(random.nextInt(3));
            sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
            dbHelper = new DBHelper(sqLiteDatabase);
            dbHelper.saveNote(noteId, date, 20000 + random.nextInt(20000), sleepQuality,
                    "Dummy is dreaming" + noteId, "","This is a dummy " + noteId);
        }
    }
    public void buttonChatGPT(View view){
        JSONObject jsonObject = new JSONObject();
        interpretTextView.setText("loading...");
        try {
            jsonObject.put("model", "gpt-3.5-turbo");

            JSONArray jsonArrayMessage = new JSONArray();
            JSONObject jsonObjectMessage = new JSONObject();
            jsonObjectMessage.put("role", "user");
            jsonObjectMessage.put("content", "Write a brief interpretation on this dream in 80 words: " +
                    dreamEditText.getText().toString());
            jsonArrayMessage.put(jsonObjectMessage);

            jsonObject.put("messages", jsonArrayMessage);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                stringURLEndPoint, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String stringText = null;
                try {
                    stringText = response.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                stringOutput = stringOutput + stringText;
                interpretTextView.setText(stringOutput);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                interpretTextView.setText("There is an error. Please try again later.");
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> mapHeader = new HashMap<>();
                mapHeader.put("Authorization", "Bearer " + stringAPIKey);
                mapHeader.put("Content-Type", "application/json");

                return mapHeader;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }
        };

        int intTimeoutPeriod = 60000; // 60 seconds timeout duration defined
        RetryPolicy retryPolicy = new DefaultRetryPolicy(intTimeoutPeriod,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjectRequest.setRetryPolicy(retryPolicy);
        Volley.newRequestQueue(getApplicationContext()).add(jsonObjectRequest);
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
}