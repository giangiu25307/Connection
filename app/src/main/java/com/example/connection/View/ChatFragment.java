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
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.R;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatFragment extends Fragment implements View.OnClickListener {

    Button messagesButton, groupsButton, requestButton;
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

        messagesButton = view.findViewById(R.id.chatButton);
        groupsButton = view.findViewById(R.id.groupsButton);
        requestButton = view.findViewById(R.id.globalButton);
        messagesButton.setOnClickListener(this);
        groupsButton.setOnClickListener(this);
        requestButton.setOnClickListener(this);

        setupRecyclerView(view);

        return view;
    }

    private void changeView(int button){
        ArrayList<Button> buttonArray = new ArrayList<>(Arrays.asList(messagesButton, groupsButton, requestButton));
        Button currentButton;
        for (int i = 0; i <= 2; i++){
            currentButton = buttonArray.get(i);
            if(button == i){
                currentButton.setBackground(getResources().getDrawable(R.drawable.chat_selector_btn_background));
                currentButton.setTextColor(Color.WHITE);
            }else{
                currentButton.setBackgroundColor(Color.TRANSPARENT);
                currentButton.setTextColor(textColor);
            }
        }

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
            case R.id.chatButton:
                changeView(0);
                break;
            case R.id.groupsButton:
                changeView(1);
                break;
            case R.id.globalButton:
                changeView(2);
                break;
            default:
                break;
        }
    }
}
