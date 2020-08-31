package com.example.connection.Controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.view.View;
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
    private ArrayList<Integer> previousX, previousY;
    private AbsoluteLayout mapLayout;
    private int widthHeight = 200;
    private ArrayList<ImageView> images;

    public DrawController(Context context, ArrayList<User> userList, AbsoluteLayout mapLayout) {
        super(context);
        this.paint = new Paint();
        this.userList = userList;
        this.previousX = new ArrayList<Integer>();
        this.previousY = new ArrayList<Integer>();
        this.mapLayout = mapLayout;
        this.postInvalidate();
        this.images = new ArrayList<ImageView>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        //canvas.drawLine(x, y, tempX, tempY, paint);
        if(Connection.boot)createCoordinates();
    }

    private void createCoordinates(){
        x = getWidth() / 2;
        y = getHeight() / 2;
        int tempX = 0, tempY = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (i == 0) createUserPoint(getWidth() / 2, getHeight() / 2, i);
            else {
                tempX = (int) (Math.random() * getWidth());
                tempY = (int) (Math.random() * getHeight());
                while (check(previousX,tempX)) tempX = (int) (Math.random() * getWidth());
                while (check(previousY,tempY)) tempY = (int) (Math.random() * getWidth());
                previousX.add(tempX);
                previousY.add(tempY);
                x = tempX;
                y = tempY;
                createUserPoint(x, y, i);
            }
        }
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
        images.add(image);
    }

    private boolean check(ArrayList<Integer> previousCoordinates, int coordinates) {
        for (int i = 0; i < previousCoordinates.size(); i++) {
            if (previousCoordinates.get(i) - (widthHeight) < coordinates && coordinates< previousCoordinates.get(i)+ (widthHeight))return true;
        }
        return false;
    }

}
