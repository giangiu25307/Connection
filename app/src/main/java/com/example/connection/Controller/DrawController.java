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

import com.example.connection.Model.MapUsers;
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
    private ArrayList<Integer> previousX, previousY;
    private ArrayList<ImageView> images;

    public DrawController(Context context, ArrayList<User> userList, AbsoluteLayout mapLayout) {
        super(context);
        this.paint = new Paint();
        this.userList = userList;
        this.mapLayout = mapLayout;
        this.postInvalidate();
        this.previousY = this.previousX = new ArrayList<Integer>();
        this.images = new ArrayList<ImageView>();
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);
        if (Connection.boot) createCoordinates(canvas);
        else {
            applyFilters(Connection.minAge,Connection.maxAge,Connection.genders);
            deleteFromMapUsers();
            addToMap();
            drawCoordinates(canvas);
        }
    }

    private void drawCoordinates(Canvas canvas) {
        for (int i = 0; i < Connection.mapUsers.size(); i++) {
            if (Connection.mapUsers.get(i).isVisible()) {
                if (i == 0) {
                    ((ViewGroup) Connection.mapUsers.get(i).getImage().getParent()).removeView(Connection.mapUsers.get(i).getImage());
                    mapLayout.addView(Connection.mapUsers.get(i).getImage());
                } else {
                    canvas.drawLine(x, y, Connection.mapUsers.get(i).getX(), Connection.mapUsers.get(i).getY(), paint);
                    ((ViewGroup) Connection.mapUsers.get(i).getImage().getParent()).removeView(Connection.mapUsers.get(i).getImage());
                    mapLayout.addView(Connection.mapUsers.get(i).getImage());
                }
                x = Connection.mapUsers.get(i).getX();
                y = Connection.mapUsers.get(i).getY();
            }
        }
    }

    private void createCoordinates(Canvas canvas) {
        x = getWidth() / 2;
        y = getHeight() / 2;
        int tempX = 0, tempY = 0;
        for (int i = 0; i < userList.size(); i++) {
            if (i == 0) {
                createUserPoint(getWidth() / 2, getHeight() / 2, i);
                Connection.mapUsers.add(new MapUsers(userList.get(i).getIdUser(), getWidth() / 2, getHeight() / 2, images.get(i), userList.get(i).getAge(), userList.get(i).getGender()));
            } else {
                tempX = (int) (Math.random() * getWidth());
                tempY = (int) (Math.random() * getHeight());
                while (check(previousX, tempX))
                    tempX = (int) (Math.random() * getWidth());
                while (check(previousY, tempY))
                    tempY = (int) (Math.random() * getHeight());
                previousX.add(tempX);
                previousY.add(tempY);
                canvas.drawLine(x, y, tempX, tempY, paint);
                x = tempX;
                y = tempY;
                createUserPoint(x, y, i);
                Connection.mapUsers.add(new MapUsers(userList.get(i).getIdUser(), x, y, images.get(i), userList.get(i).getAge(), userList.get(i).getGender()));
            }
        }
        Connection.boot = false;
    }

    //create a clickable item who refers to a user at the coordinates x,y
    private void createUserPoint(final int x, final int y, final int id) {
        final ImageView image = new ImageView(mapLayout.getContext());
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
                String ageGender = user.getAge() + ", " + user.getGender();
                information.setText(ageGender);
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
        mapLayout.addView(image);
    }

    //check coordinates distances
    private boolean check(ArrayList<Integer> previousCoordinates, int coordinates) {
        int count=0;
        for (int i = 0; i < previousCoordinates.size(); i++) {
            if (previousCoordinates.get(i) - (widthHeight) < coordinates && coordinates < previousCoordinates.get(i)-50 || previousCoordinates.get(i)+50 < coordinates && coordinates < previousCoordinates.get(i) + (widthHeight)) count++;
        }
        if(count==previousCoordinates.size())return true;
        else return false;
    }

    //ADDING NEW USERS TO THE MAP

    private ArrayList<String> getAllUserIds() {
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < userList.size(); i++) {
            ids.add(userList.get(i).getIdUser());
        }
        return ids;
    }

    private boolean mapContainsId(String id) {
        for (int i = 0; i < Connection.mapUsers.size(); i++) {
            if (Connection.mapUsers.get(i).getId().equals(id)) return true;
        }
        return false;
    }

    private ArrayList<User> userToAdd() {
        ArrayList<String> ids = getAllUserIds();
        ArrayList<User> userList = new ArrayList<User>();
        for (int i = 0; i < ids.size(); i++) {
            if (!mapContainsId(ids.get(i))) {
                for (int j = 0; j < userList.size(); j++) {
                    if (userList.get(j).getIdUser().equals(ids.get(i)))
                        userList.add(userList.get(j));
                }
            }
        }
        return userList;
    }

    private void addToMap() {
        previousX = previousY = new ArrayList<Integer>();
        images = new ArrayList<ImageView>();
        for (int i = 0; i < Connection.mapUsers.size(); i++) {
            previousX.add(Connection.mapUsers.get(i).getX());
            previousY.add(Connection.mapUsers.get(i).getY());
        }
        ArrayList<User> userList = userToAdd();
        for (int i = 0; i < userList.size(); i++) {
            x = (int) (Math.random() * getWidth());
            y = (int) (Math.random() * getHeight());
            while (check(previousX, x))
                x = (int) (Math.random() * getWidth());
            while (check(previousY, y))
                y = (int) (Math.random() * getHeight());
            previousX.add(x);
            previousY.add(y);
            createUserPoint(x, y, i);
            Connection.mapUsers.add(new MapUsers(userList.get(i).getIdUser(), x, y, images.get(i), userList.get(i).getAge(), userList.get(i).getGender()));
        }

    }

    //DELETE OLD USERS FROM THE MAP
    private ArrayList<String> getAllMapIds() {
        ArrayList<String> ids = new ArrayList<String>();
        for (int i = 0; i < Connection.mapUsers.size(); i++) {
            ids.add(Connection.mapUsers.get(i).getId());
        }
        return ids;
    }

    private boolean userListContainsId(String id) {
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getIdUser().equals(id)) return true;
        }
        return false;
    }

    private ArrayList<String> userToDelete() {
        ArrayList<String> ids = getAllMapIds();
        ArrayList<String> userIdList = new ArrayList<String>();
        for (int i = 0; i < ids.size(); i++) {
            if (!userListContainsId(ids.get(i))) userIdList.add(ids.get(i));
        }
        return userIdList;
    }

    private void deleteFromMapUsers() {
        ArrayList<String> userIdList = userToDelete();
        for (int i = 0; i < Connection.mapUsers.size(); i++) {
            for (int j = 0; j < userIdList.size(); j++) {
                if (Connection.mapUsers.get(i).getId().equals(userIdList.get(j)))
                    Connection.mapUsers.remove(Connection.mapUsers.get(i));
            }
        }
    }

    //APPLY FILTERS ON THE MAP
    public void applyFilters(String minAge, String maxAge, String[] genders) {
        if (minAge.equals("") && maxAge.equals("") && genders[0].equals("") && genders[1].equals("") && genders[2].equals("")) {
            for (int i = 1; i < Connection.mapUsers.size(); i++)
                Connection.mapUsers.get(i).setVisibility(true);
        } else {
            for (int i = 1; i < Connection.mapUsers.size(); i++)
                Connection.mapUsers.get(i).setVisibility(false);
            if (!minAge.equals("")) {
                for (int i = 1; i < Connection.mapUsers.size(); i++)
                    if (Integer.parseInt(Connection.mapUsers.get(i).getAge()) >= Integer.parseInt(minAge))
                        Connection.mapUsers.get(i).setVisibility(true);
            }
            if (!maxAge.equals("")) {
                for (int i = 1; i < Connection.mapUsers.size(); i++)
                    if (Integer.parseInt(Connection.mapUsers.get(i).getAge()) <= Integer.parseInt(maxAge))
                        Connection.mapUsers.get(i).setVisibility(true);
            }
            if (!genders[0].equals("")){
                for (int i = 1; i < Connection.mapUsers.size(); i++)if (Connection.mapUsers.get(i).getGender().equals(genders[0]))Connection.mapUsers.get(i).setVisibility(true);
            }
            if (!genders[1].equals("")){
                for (int i = 1; i < Connection.mapUsers.size(); i++)if (Connection.mapUsers.get(i).getGender().equals(genders[1]))Connection.mapUsers.get(i).setVisibility(true);
            }
            if (!genders[2].equals("")){
                for (int i = 1; i < Connection.mapUsers.size(); i++)if (Connection.mapUsers.get(i).getGender().equals(genders[2]))Connection.mapUsers.get(i).setVisibility(true);
            }

        }
    }

}
