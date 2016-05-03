package com.tr.timemanagement.model;

public class User {

    private String date;
    private String hours;
    private String notes;

    public User() {
    }

    public User(String date, String hours, String notes) {
        this.date = date;
        this.hours = hours;
        this.notes = notes;
    }

    public String getDate() {
        return date;
    }

    public String getHours() {
        return hours;
    }

    public String getNotes() {
        return notes;
    }

}
