package com.cs407.oyasumi;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    static SQLiteDatabase sqLiteDatabase;
    public DBHelper (SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }
    public static void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes " +
                "(id INTEGER PRIMARY KEY, noteId INTEGER, date TEXT, sleepDuration INTEGER, sleepQuality TEXT, dreamNote TEXT, additionalNote TEXT)");
    }
    public ArrayList<Note> readNotes() {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM notes", new String[]{});
        int noteIndex = c.getColumnIndex("noteId");
        int dateIndex = c.getColumnIndex("date");
        int sleepDurationIndex = c.getColumnIndex("sleepDuration");
        int sleepQualityIndex = c.getColumnIndex("sleepQuality");
        int dreamNoteIndex = c.getColumnIndex("dreamNote");
        int additionalNoteIndex = c.getColumnIndex("additionalNote");
        c.moveToFirst();
        ArrayList<Note> notesList = new ArrayList<>();
        while (!c.isAfterLast()) {
            int noteId = c.getInt(noteIndex);
            String date = c.getString(dateIndex);
            int sleepDuration = c.getInt(sleepDurationIndex);
            String sleepQuality = c.getString(sleepQualityIndex);
            String dreamNote = c.getString(dreamNoteIndex);
            String additionalNote = c.getString(additionalNoteIndex);
            Note notes = new Note(noteId, date, sleepDuration, sleepQuality, dreamNote, additionalNote);
            notesList.add(notes);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();
        return notesList;
    }
    public void saveNote(int noteId, String date, int sleepDuration, String sleepQuality, String dreamNote, String additionalNote) {
        createTable();
        sqLiteDatabase.execSQL("INSERT INTO notes (noteId, date, sleepDuration, sleepQuality, dreamNote, additionalNote) VALUES (?, ?, ?, ?, ?, ?)",
                new String[]{String.valueOf(noteId), date, String.valueOf(sleepDuration), sleepQuality, dreamNote, additionalNote});
    }
    public void updateNote(int noteId, String date, int sleepDuration, String sleepQuality, String dreamNote, String additionalNote) {
        createTable();
        Note note = new Note(noteId, date, sleepDuration, sleepQuality, dreamNote, additionalNote);
        sqLiteDatabase.execSQL("UPDATE notes set date=?, sleepDuration=?, sleepQuality=?, dreamNote=?, additionalNote=? WHERE noteId=?",
                new String[]{date, String.valueOf(sleepDuration), sleepQuality, dreamNote, additionalNote, String.valueOf(noteId)});
    }
    public void deleteNote(int noteId) {
        createTable();
        String date = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT date FROM notes WHERE noteId = ?",
                new String[]{String.valueOf(noteId)});
        if (cursor.moveToNext()) {
            date = cursor.getString(0);
        }
        sqLiteDatabase.execSQL("DELETE FROM notes WHERE noteId = ?",
                new String[]{String.valueOf(noteId)});
        cursor.close();
    }
}