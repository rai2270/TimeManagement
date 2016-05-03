package com.tr.timemanagement.model;

/**
 * TODO: Add a class header comment!
 */
public class UserModel { //implements ModelKeyInterface {

    //private String key;
    private String email;
    private String name;
    private String prefHours;
    private int level;
    private String manager;

    // Must have constructor!!! for firebase to work
    public UserModel() {
    }

    public UserModel(String prefHours) {
        this.prefHours = prefHours;

    }

    /*public String getKey() {
        return key;
    }
    public void setKey(String key) { this.key = key; }*/

    public String getEmail() { return email; }
    public void setEmail(String email) {this.email = email;}

    public String getName() { return name; }
    public void setName(String name) {this.name = name;}

    public String getPrefHours() { return prefHours; }
    public void setPrefHours(String prefHours) {this.prefHours = prefHours;}

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
