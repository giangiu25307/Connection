package com.example.connection.Model;

public class GroupOwner {

    private String id, password, SSID;

    public GroupOwner(String id, String SSID, String password) {
        this.id = id;
        this.password = password;
        this.SSID = SSID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }
}
