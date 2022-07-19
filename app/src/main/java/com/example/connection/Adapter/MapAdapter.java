package com.example.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.connection.Model.User;
import com.example.connection.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MapAdapter extends ArrayAdapter<User> {

    public MapAdapter(@NonNull Context context, ArrayList<User> userArray) {
        super(context, 0, userArray);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        view = LayoutInflater.from(getContext()).inflate(R.layout.lyt_map_user, parent, false);
        User user = getItem(position);
        ShapeableImageView userProfileIcon = view.findViewById(R.id.userProfileIcon);
        Bitmap bitmap = BitmapFactory.decodeFile(user.getProfilePic());
        Drawable draw = new BitmapDrawable(getContext().getResources(), bitmap);
        userProfileIcon.setImageTintList(null);
        userProfileIcon.setImageDrawable(draw);
        //userProfileIcon.setImageDrawable(Drawable.createFromPath(user.getProfilePic()));
        TextView usernameTextView = view.findViewById(R.id.usernameTextView);
        usernameTextView.setText(user.getUsername());
        return view;
    }

}
