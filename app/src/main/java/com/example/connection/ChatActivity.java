package com.example.connection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.Database;

public class ChatActivity extends AppCompatActivity{

    SharedPreferences sharedPreferences;
    ChatController chatController=ChatController.getInstance();
    String id;
    EditText message_input;
    ImageView sendView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.activity_chat);
        id = getIntent().getStringExtra("idChat");
        String name = getIntent().getStringExtra("name");
        Database database = new Database(this);
        TextView nameTextView = findViewById(R.id.nameUser);
        nameTextView.setText(name);
        ImageView imageView = findViewById(R.id.backImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        message_input =findViewById(R.id.message_input);
        sendView= findViewById(R.id.sendView);
        message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(message_input.getText().toString().replace(" ","").isEmpty()){
                    sendView.setAlpha(0.5f);
                    sendView.setClickable(false);
                }else{
                    sendView.setAlpha(1f);
                    sendView.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            chatController.sendTCPMsg(message_input.getText().toString(),id);

            }
        });
        //Database database = (Database) getIntent().getParcelableExtra("database");
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
            System.out.println("Dark");
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
        Cursor messageCursor=database.getAllMsg(id);
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        MessageAdapter chatAdapter = new MessageAdapter(this, database, id,messageCursor);
        recyclerView.setAdapter(chatAdapter);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(messageCursor.getCount());
    }



}