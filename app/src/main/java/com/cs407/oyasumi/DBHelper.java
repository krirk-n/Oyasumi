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
                "(id INTEGER PRIMARY KEY, noteId INTEGER, date TEXT, sleepDuration INTEGER, sleepQuality TEXT, additionalNote TEXT)");
    }
    public ArrayList<Note> readNotes() {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM notes", new String[]{});
        int noteIndex = c.getColumnIndex("noteId");
        int dateIndex = c.getColumnIndex("date");
        int sleepDurationIndex = c.getColumnIndex("sleepDuration");
        int sleepQualityIndex = c.getColumnIndex("sleepQuality");
        int additionalNoteIndex = c.getColumnIndex("additionalNote");
        c.moveToFirst();
        ArrayList<Note> notesList = new ArrayList<>();
        while (!c.isAfterLast()) {
            int noteId = c.getInt(noteIndex);
            String date = c.getString(dateIndex);
            int sleepDuration = c.getInt(sleepDurationIndex);
            String sleepQuality = c.getString(sleepQualityIndex);
            String additionalNote = c.getString(additionalNoteIndex);
            Note notes = new Note(noteId, date, sleepDuration, sleepQuality, additionalNote);
            notesList.add(notes);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();
        return notesList;
    }
    public void saveNotes(int noteId, String date, int sleepDuration, String sleepQuality, String additionalNote) {
        createTable();
        sqLiteDatabase.execSQL("INSERT INTO notes (noteId, date, sleepDuration, sleepQuality, additionalNote) VALUES (?, ?, ?, ?, ?)",
                new String[]{String.valueOf(noteId), date, String.valueOf(sleepDuration), sleepQuality, additionalNote});
    }
    public void updateNotes(int noteId, String date, int sleepDuration, String sleepQuality, String additionalNote) {
        createTable();
        Note note = new Note(noteId, date, sleepDuration, sleepQuality, additionalNote);
        sqLiteDatabase.execSQL("UPDATE notes set date=?, sleepDuration=?, sleepQuality=?, additionalNote=? WHERE noteId=?",
                new String[]{date, String.valueOf(sleepDuration), sleepQuality, additionalNote, String.valueOf(noteId)});
    }
    public void deleteNotes(int noteId) {
        createTable();
        String date = "";
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT date FROM notes WHERE noteId = ?",
                new String[]{String.valueOf(noteId)});
        if (cursor.moveToNext()) {
            date = cursor.getString(0);
        }
        sqLiteDatabase.execSQL("DELETE FROM notes WHERE noteId = ? AND date =?",
                new String[]{String.valueOf(noteId), date});
        cursor.close();
    }
}