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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.View.ChatActivity;

import java.util.ArrayList;

public class DrawController extends View {

    private Paint paint;
    private ArrayList<User> userList;
    private int x, y;
    private ArrayList<Integer> previousX, previousY;
    private ConstraintLayout mapLayout;

    public DrawController(Context context, ArrayList<User> userList, ConstraintLayout mapLayout) {
        super(context);
        paint = new Paint();
        this.userList = userList;
        previousX = new ArrayList<Integer>();
        previousY = new ArrayList<Integer>();
        this.mapLayout = mapLayout;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        x = getWidth() / 2;
        y = getHeight() / 2;
        int tempX = 0, tempY = 0;
        for (int i = 0; i < userList.size(); i++) {
            tempX = (int) (Math.random() * getWidth());
            tempY = (int) (Math.random() * getHeight());
            if (previousX.contains(tempX)) tempX = (int) (Math.random() * getWidth());
            else previousX.add(tempX);
            if (previousY.contains(tempY)) tempY = (int) (Math.random() * getWidth());
            else previousY.add(tempY);
            canvas.drawLine(x, y, tempX, tempY, paint);
            x = tempX;
            y = tempY;
            createUserPoint(x, y, i);
        }
    }

    //create a clickable item who refers to a user at the coordinates x,y
    private void createUserPoint(int x, int y, final int id) {
        ImageView image = new ImageView(super.getContext());
        image.setBackground(getResources().getDrawable(R.drawable.ic_user_map));
        image.setId(id);
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
                        myIntent.putExtra("idChat", id);
                        myIntent.putExtra("name", user.getName());
                        getContext().startActivity(myIntent);
                    }
                });
            }
        });
        mapLayout.addView(image, x, y);
    }

}
