package com.example.connection.Controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;

import com.example.connection.Model.User;

import java.util.ArrayList;

public class DrawController extends View {

    private Paint paint;
    private ArrayList<User> userList;
    private int x = getWidth() / 2, y = getHeight() / 2;

    public DrawController(Context context, ArrayList<User> userList) {
        super(context);
        paint = new Paint();
        this.userList = userList;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        int tempX = 0, tempY = 0;
        for (int i = 0; i < userList.size(); i++) {
            //forse meglio creare un arraylist delle vecchie coordinate per fare in modo che non si sovrappongano mai
            tempX = (int) (Math.random() * getWidth());
            tempY = (int) (Math.random() * getHeight());
            canvas.drawLine(x, y, tempX, tempY, paint);
            x = tempX;
            y = tempY;
        }
    }

    private void createUserPoint(int x, int y) { //create a clickable item who refers to a user at the coordinates x,y
        ImageView image = new ImageView(super.getContext());
    }

}
