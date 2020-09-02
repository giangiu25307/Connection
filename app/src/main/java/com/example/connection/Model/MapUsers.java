package com.example.connection.Model;

import android.widget.ImageView;

import java.util.ArrayList;

public class MapUsers {

    private String id;
    private int x, y;
    private ImageView image;

    public MapUsers(String id,int x,int y,ImageView image) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.id = id;
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
}
