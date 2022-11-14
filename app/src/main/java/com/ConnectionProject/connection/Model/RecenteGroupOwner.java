package com.ConnectionProject.connection.Model;

public class RecenteGroupOwner {

    private String id;
    private int time;

    public RecenteGroupOwner(String id, int time) {
        this.id = id;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
