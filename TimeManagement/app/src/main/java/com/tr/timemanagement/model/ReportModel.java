package com.tr.timemanagement.model;

public class ReportModel implements ModelKeyInterface {

    private String key;
    //private String date;
    private String uid;
    private String hours;
    //private String notes;
    //private String name;

    public ReportModel() {
    }

    public ReportModel(String key, String uid, String hours) {
        this.key = key;
        this.uid = uid;
        this.hours = hours;
    }

    public String getKey() {return key;}
    public void setKey(String key) {this.key = key;}

    public String getUid() { return uid; }
    public void setUid(String uid) {this.uid = uid;}

    public String getHours() { return hours; }
    public void setHours(String hours) {this.hours = hours;}
/*
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {this.notes = notes;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/
}
