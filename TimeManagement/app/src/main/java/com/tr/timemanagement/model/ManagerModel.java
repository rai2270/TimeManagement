package com.tr.timemanagement.model;

/**
 * TODO: Add a class header comment!
 */
public class ManagerModel implements ModelKeyInterface {

    private String key;
    private String name;
    private String email;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private ManagerModel() {
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) { this.key = key; }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
