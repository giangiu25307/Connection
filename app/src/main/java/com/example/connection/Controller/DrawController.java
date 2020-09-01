package com.example.connection.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.View.ChatActivity;
import com.example.connection.View.Connection;

import java.util.ArrayList;

public class DrawController extends View {

    private Paint paint;
    private ArrayList<User> userList;
    private int x, y;
    private AbsoluteLayout mapLayout;
    private int widthHeight = 200;

    public DrawController(Context context, ArrayList<User> userList, AbsoluteLayout mapLayout) {
        super(context);
        this.paint = new Paint();
        this.userList = userList;
        this.mapLayout = mapLayout;
        this.postInvalidate();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        //
        if (Connection.boot) createCoordinates(canvas);
        else drawCoordinates(canvas);
    }

    private void drawCoordinates(Canvas canvas) {
        for (int i = 0; i < Connection.previousX.size(); i++) {
            if (i == 0) {
                ((ViewGroup) Connection.images.get(i).getParent()).removeView(Connection.images.get(i));
                mapLayout.addView(Connection.images.get(i));
                x = getWidth() / 2;
                y = getHeight() / 2;
                canvas.drawLine(x, y, Connection.previousX.get(i), Connection.previousY.get(i), paint);
                x = Connection.previousX.get(i);
                y = Connection.previousY.get(i);
            } else {
                canvas.drawLine(x, y, Connection.previousX.get(i), Connection.previousY.get(i), paint);
                ((ViewGroup) Connection.images.get(i).getParent()).removeView(Connection.images.get(i));
                mapLayout.addView(Connection.images.get(i));
                x = Connection.previousX.get(i);
                y = Connection.previousY.get(i);
            }
        }
        ((ViewGroup) Connection.images.get(Connection.previousX.size()).getParent()).removeView(Connection.images.get(Connection.previousX.size()));
        mapLayout.addView(Connection.images.get(Connection.previousX.size()));
    }

    private void createCoordinates(Canvas canvas) {
        x = getWidth() / 2;
        y = getHeight() / 2;
        int tempX = 0, tempY = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (i == 0) createUserPoint(getWidth() / 2, getHeight() / 2, i);
            else {
                tempX = (int) (Math.random() * getWidth());
                tempY = (int) (Math.random() * getHeight());
                while (check(Connection.previousX, tempX))
                    tempX = (int) (Math.random() * getWidth());
                while (check(Connection.previousY, tempY))
                    tempY = (int) (Math.random() * getHeight());
                Connection.previousX.add(tempX);
                Connection.previousY.add(tempY);
                canvas.drawLine(x, y, tempX, tempY, paint);
                x = tempX;
                y = tempY;
                createUserPoint(x, y, i);
            }
        }
        Connection.boot = false;
    }

    //create a clickable item who refers to a user at the coordinates x,y
    private void createUserPoint(final int x, final int y, final int id) {
        final ImageView image = new ImageView(mapLayout.getContext());
        //image.requestLayout();
        Bitmap bitmap = BitmapFactory.decodeFile(userList.get(id).getProfilePic());
        if (bitmap != null) {
            image.setImageBitmap(bitmap);
        }

        image.requestLayout();
        ViewTreeObserver vto = image.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                image.getViewTreeObserver().removeOnPreDrawListener(this);
                AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(widthHeight, widthHeight, x - 100, y - 100);
                image.setLayoutParams(params);
                int finalWidth = image.getLayoutParams().width;
                int finalHeight = image.getLayoutParams().height;
                System.out.println("Height: " + finalHeight + " Width: " + finalWidth);
                return true;
            }
        });
        image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.person_found_alert_dialog);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                final User user = userList.get(id);
                ImageView profilePic = alertDialog.findViewById(R.id.profilePicMapAlertDialog);
                TextView name = alertDialog.findViewById(R.id.nameMapAlertDialog);
                TextView information = alertDialog.findViewById(R.id.informationMapAlertDialog);
                TextView send = alertDialog.findViewById(R.id.sendMessageMapAlertDialog);
                Bitmap bitmap = BitmapFactory.decodeFile(user.getProfilePic());
                Drawable draw = new BitmapDrawable(getResources(), bitmap);
                profilePic.setImageTintList(null);
                profilePic.setImageDrawable(draw);
                name.setText(user.getUsername());
                information.setText(user.getGender());
                send.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(getContext(), ChatActivity.class);
                        myIntent.putExtra("idChat", user.getIdUser());
                        myIntent.putExtra("name", user.getName());
                        getContext().startActivity(myIntent);
                    }
                });
            }
        });
        Connection.images.add(image);
        mapLayout.addView(image);
    }

    private boolean check(ArrayList<Integer> previousCoordinates, int coordinates) {
        for (int i = 0; i < previousCoordinates.size(); i++) {
            if (previousCoordinates.get(i) - (widthHeight) < coordinates && coordinates < previousCoordinates.get(i) + (widthHeight))
                return true;
        }
        return false;
    }

}
