package com.example.connection.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class ChatActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ChatController chatController = ChatController.getInstance();
    private String id;
    private EditText message_input;
    private ImageView sendView;
    private RecyclerView recyclerView;
    private Database database;
    private ConstraintLayout chatBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.activity_chat);
        id = getIntent().getStringExtra("idChat");
        String name = getIntent().getStringExtra("name");
        database = new Database(this);
        TextView nameTextView = findViewById(R.id.nameUser);
        nameTextView.setText(name);
        ImageView imageView = findViewById(R.id.backImageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        message_input = findViewById(R.id.message_input);
        sendView = findViewById(R.id.sendView);
        chatBackground = findViewById(R.id.chatBackground);
        message_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (message_input.getText().toString().replace(" ", "").isEmpty()) {
                    sendView.setAlpha(0.5f);
                    sendView.setClickable(false);
                } else {
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

                chatController.sendTCPMsg(message_input.getText().toString(), id);

            }
        });
        //Database database = (Database) getIntent().getParcelableExtra("database");
        setupRecyclerView(database, id);

    }


    private void loadTheme() {
        String theme = sharedPreferences.getString("appTheme", "light");
        if (theme.equals("light")) {
            setTheme(R.style.AppTheme);
            setStatusAndNavbarColor(true);
        } else if (theme.equals("dark")) {
            setTheme(R.style.DarkTheme);
            setStatusAndNavbarColor(false);
            System.out.println("Dark");
        } else {
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

    private void setStatusAndNavbarColor(boolean light) {
        Window window = getWindow();
        if (light) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.WHITE);
            window.setNavigationBarColor(Color.WHITE);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.setStatusBarColor(getColor(R.color.mediumBlack));
        }
        window.setNavigationBarColor(Color.BLACK);
    }

    private void setupRecyclerView(Database database, String id) {
        Cursor messageCursor = database.getAllMsg(id);
        recyclerView = findViewById(R.id.messageRecyclerView);
        setBackgroundImage();
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        MessageAdapter chatAdapter = new MessageAdapter(this, database, id, messageCursor, linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.scrollToPosition(messageCursor.getCount() - 1);
    }

    private void setBackgroundImage(){
        Cursor c=database.getBacgroundImage();
        if(c==null||c.getCount()==0)return;
        c.moveToLast();
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(0));
        Drawable draw = new BitmapDrawable(getResources(), bitmap);
        chatBackground.setBackground(draw);
        c.close();
    }

}