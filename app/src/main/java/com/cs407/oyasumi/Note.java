package com.cs407.oyasumi;

public class Note {
    private int noteId;
    private String date;
    private int sleepDuration;
    private String sleepQuality;
    private String dreamNote;
    private String dreamInterpret;
    private String additionalNote;
    public Note(int noteId, String date, int sleepDuration, String sleepQuality, String dreamNote,
                String dreamInterpret, String additionalNote){
        this.noteId = noteId;
        this.date = date;
        this.sleepDuration = sleepDuration;
        this.sleepQuality = sleepQuality;
        this.dreamNote = dreamNote;
        this.dreamInterpret = dreamInterpret;
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
    public String getDreamNote() { return dreamNote;}
    public String getDreamInterpret() { return dreamInterpret;}
    public String getAdditionalNote() {
        return additionalNote;
    }
}
