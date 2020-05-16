package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private ImageView globalButton;
    private int textColor;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.chat_fragment, null);

        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("appTheme", "light").equals("light")){
            textColor = Color.BLACK;
        }else{
            textColor = Color.WHITE;
        }

        globalButton = view.findViewById(R.id.globalButton);
        globalButton.setOnClickListener(this);

        //setupRecyclerView(view);

        return view;
    }

    private void changeView(int button){

    }

    private void setupRecyclerView(View view){

        RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);
        ChatAdapter chatAdapter = new ChatAdapter(getContext(), getAllChat());

    }

    private Cursor getAllChat(){

        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.globalButton:
                //changeView(2);
                break;
            default:
                break;
        }
    }
}
