package com.example.connection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.Database;

public class ChatActivity extends AppCompatActivity{

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String id = getIntent().getStringExtra("idChat");
        String name = getIntent().getStringExtra("name");
        TextView nameTextView = findViewById(R.id.nameUser);
        nameTextView.setText(name);
        ImageView imageView = findViewById(R.id.backImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Database database = (Database) getIntent().getParcelableExtra("database");
        Database database = new Database(this);
        loadTheme();
        setupRecyclerView(database, id);

    }



    private void loadTheme(){
        String theme = sharedPreferences.getString("appTheme", "light");
        if(theme.equals("light")){
            setTheme(R.style.AppTheme);
            setStatusAndNavbarColor(true);
        }else if(theme.equals("dark")){
            setTheme(R.style.DarkTheme);
            setStatusAndNavbarColor(false);
        }else{
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_NO:
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    setTheme(R.style.AppTheme);
                    setStatusAndNavbarColor(true);
                    break;
                case Configuration.UI_MODE_NIGHT_YES:
                    setTheme(R.style.DarkTheme);
                    setStatusAndNavbarColor(false);
                    break;
                default:
                    break;
            }
        }

    }

    private void setStatusAndNavbarColor(boolean light){
        Window window = getWindow();
        if(light){
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }else{
            int color = getColor(R.color.lightBlack);
            window.setNavigationBarColor(color);
            window.setStatusBarColor(color);
        }
    }

    private void setupRecyclerView(Database database, String id){
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        MessageAdapter chatAdapter = new MessageAdapter(this, database, id);
        recyclerView.setAdapter(chatAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(35);
    }



}