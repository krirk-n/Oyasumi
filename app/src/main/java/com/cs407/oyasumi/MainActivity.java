package com.cs407.oyasumi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    TextView dateTextView;
    TextView sleepDurationTextView;
    TextView sleepQualityTextView;
    EditText dreamEditText;
    EditText additionEditText;
    FloatingActionButton prevFloatingActionButton;
    FloatingActionButton nextFloatingActionButton;
    Note currentNote;
    private int currentPage = -1;
    private int notesSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        createDummyNotes(3);

        prevFloatingActionButton = (FloatingActionButton) findViewById(R.id.prevFloatingActionButton);
        nextFloatingActionButton = (FloatingActionButton) findViewById(R.id.nextFloatingActionButton);

        prevFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage--;
                Log.i("currentPage", String.valueOf(currentPage));
                saveDreamAndAdditionalNote();
                resetUI();
                checkButtons();
            }
        });
        nextFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPage++;
                Log.i("currentPage", String.valueOf(currentPage));
                saveDreamAndAdditionalNote();
                resetUI();
                checkButtons();
            }
        });

        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Note> notes = dbHelper.readNotes();
        notesSize = notes.size();
        currentPage = notesSize-1;
        Log.i("currentPage", "Current page: " + String.valueOf(currentPage));
        Note recentNote = notes.get(currentPage); // show the recent note
        currentNote = recentNote; // will change after having todayPage
        checkButtons();
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        sleepDurationTextView = (TextView) findViewById(R.id.sleepDurationTextView);
        sleepQualityTextView = (TextView) findViewById(R.id.sleepQualityTextView);
        dreamEditText = (EditText) findViewById(R.id.dreamEditText);
        additionEditText = (EditText) findViewById(R.id.additionEditText);
        dateTextView.setText(recentNote.getDate());
        sleepDurationTextView.setText("I've slept for " + recentNote.getSleepDuration() / 3600 + " hours "
            + (recentNote.getSleepDuration() % 3600) / 60 + " minutes."); // assume we get seconds from sleep duration
        sleepQualityTextView.setText("My sleep quality was " + recentNote.getSleepQuality() + ".");
        dreamEditText.setText(recentNote.getDreamNote());
        additionEditText.setText(recentNote.getAdditionalNote());
    }

    public void checkButtons() {
        prevFloatingActionButton.setEnabled(currentPage > 0);
        nextFloatingActionButton.setEnabled(currentPage < notesSize-1); // -1
    }

    public void resetUI() {
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        ArrayList<Note> notes = dbHelper.readNotes();
        currentNote = notes.get(currentPage); // show the recent note
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        sleepDurationTextView = (TextView) findViewById(R.id.sleepDurationTextView);
        sleepQualityTextView = (TextView) findViewById(R.id.sleepQualityTextView);
        dreamEditText = (EditText) findViewById(R.id.dreamEditText);
        additionEditText = (EditText) findViewById(R.id.additionEditText);
        dateTextView.setText(currentNote.getDate());
        sleepDurationTextView.setText("I've slept for " + currentNote.getSleepDuration() / 3600 + " hours "
                + (currentNote.getSleepDuration() % 3600) / 60 + " minutes."); // assume we get seconds from sleep duration
        sleepQualityTextView.setText("My sleep quality was " + currentNote.getSleepQuality() + ".");
        dreamEditText.setText(currentNote.getDreamNote());
        additionEditText.setText(currentNote.getAdditionalNote());
    }

    public void saveDreamAndAdditionalNote() {
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("notes", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        String newAdditionalNote = additionEditText.getText().toString();
        String newDreamNote = dreamEditText.getText().toString();
        Log.i("add", "new: " + newAdditionalNote);
        dbHelper.updateNote(currentNote.getNoteId(), currentNote.getDate(), currentNote.getSleepDuration(),
                currentNote.getSleepQuality(), newDreamNote, newAdditionalNote);
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
                    "Dummy is dreaming" + noteId, "This is a dummy " + noteId);
        }
    }
}