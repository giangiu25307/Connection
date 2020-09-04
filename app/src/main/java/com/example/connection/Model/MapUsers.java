package com.example.connection.Model;

import android.widget.ImageView;

import java.util.ArrayList;

public class MapUsers {

    private String id;
    private int x, y;
    private ImageView image;
    private boolean visibility;
    private String age,gender;

    public MapUsers(String id, int x, int y, ImageView image, String age, String gender) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.id = id;
        this.visibility = true;
        this.age = age;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public ImageView getImage() {
        return image;
    }

    public boolean isVisible() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
