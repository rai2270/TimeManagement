package com.tr.timemanagement.model;

public class DateModel implements ModelKeyInterface {

    private String key;
    private String hours;
    private String notes;
    //private String reportDatesPostId;

    public DateModel() {
    }

    public DateModel(String hours, String notes) {
        this.hours = hours;
        this.notes = notes;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /*
    public String getReportDatesPostId() {
        return reportDatesPostId;
    }

    public void setReportDatesPostId(String reportDatesPostId) {
        this.reportDatesPostId = reportDatesPostId;
    }*/

}
