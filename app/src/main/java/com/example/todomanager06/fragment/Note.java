package com.example.todomanager06.fragment;

public class Note {
    private String tasks;
    private String date;
    private String repeat;

    public Note(String tasks, String date, String repeat) {
        this.tasks = tasks;
        this.date = date;
        this.repeat = repeat;
    }

    public String getTasks() {
        return tasks;
    }

    public String getDate() {
        return date;
    }

    public String getRepeat() {
        return repeat;
    }
}
