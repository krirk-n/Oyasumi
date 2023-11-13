package com.cs407.oyasumi;

public class Note {
    private int noteId;
    private String date;
    private int sleepDuration;
    private String sleepQuality;
    private String additionalNote;
    public Note(int noteId, String date, int sleepDuration, String sleepQuality, String additionalNote){
        this.noteId = noteId;
        this.date = date;
        this.sleepDuration = sleepDuration;
        this.sleepQuality = sleepQuality;
        this.additionalNote = additionalNote;
    }
    public int getNoteId() {
        return noteId;
    }
    public String getDate() {
        return date;
    }
    public int getSleepDuration() {
        return sleepDuration;
    }
    public String getSleepQuality() {
        return sleepQuality;
    }
    public String getAdditionalNote() {
        return additionalNote;
    }
}
