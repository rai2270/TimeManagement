package com.tr.timemanagement.model;

public class Report2Model {

    private String name;
    private String uid;
    private String hours;
    private long time;
    private String date;

    public Report2Model() {
    }

    public Report2Model(String name, String uid, String hours, long time, String date) {
        this.name = name;
        this.uid = uid;
        this.hours = hours;
        this.time = time;
        this.date = date;
    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getUid() { return uid; }
    public void setUid(String uid) {this.uid = uid;}

    public String getHours() { return hours; }
    public void setHours(String hours) {this.hours = hours;}

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


}
