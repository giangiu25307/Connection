package com.example.connection.View;

import android.annotation.SuppressLint;
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
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.GlobalMessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Listener.MessageListener;
import com.example.connection.R;

public class ChatGlobalActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ChatController chatController = ChatController.getInstance();
    private String id;
    private TextView noMessageTextView;
    private EditText message_input;
    private ImageButton sendView;
    private ImageView noMessageImageView;
    private RecyclerView recyclerView;
    private GlobalMessageAdapter globalMessageAdapter;
    private ConstraintLayout chatBackground;
    private int lastPosition;
    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.lyt_chat_global);
        ImageView imageView = findViewById(R.id.backImageView);
        imageView.setOnClickListener(view -> finish());
        message_input = findViewById(R.id.message_input);
        sendView = findViewById(R.id.sendView);
        chatBackground = findViewById(R.id.chatBackground);
        message_input.setOnTouchListener((view, event) -> {
            try {
                lastPosition = linearLayoutManager.findLastVisibleItemPosition();
            }catch (IllegalArgumentException e){
                System.out.println(e);
            }
            return false;
        });
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

        noMessageImageView = findViewById(R.id.noMessageImageView);
        noMessageTextView = findViewById(R.id.noMessageTextView);
        setupRecyclerView();

        sendView.setOnClickListener(view -> {
            chatController.sendGlobalMsg(message_input.getText().toString());
            MessageListener.getIstance().setGlobalMessageAdapter(globalMessageAdapter);
        });

        //Database database = (Database) getIntent().getParcelableExtra("database");

    }


    private void loadTheme() {
        String theme = sharedPreferences.getString("appTheme", "light");
        if (theme.equals("light")) {
            setTheme(R.style.AppTheme);
            setStatusAndNavbarColor(true);
        } else if (theme.equals("dark")) {
            setTheme(R.style.DarkTheme);
            setStatusAndNavbarColor(false);
        } else {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                setTheme(R.style.DarkTheme);
                setStatusAndNavbarColor(false);
            } else {
                setTheme(R.style.AppTheme);
                setStatusAndNavbarColor(true);
            }
        }

    }

    private void setStatusAndNavbarColor(boolean light) {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (light) {
            window.setStatusBarColor(getColor(R.color.colorPrimary));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            window.setStatusBarColor(getColor(R.color.darkColorPrimary));
        }
        window.setNavigationBarColor(Color.BLACK);
    }

    private void setupRecyclerView() {
        Cursor messageCursor = getAllMessage();
        recyclerView = findViewById(R.id.messageRecyclerView);
        setBackgroundImage();
        if(messageCursor != null && messageCursor.getCount() > 0){
            globalMessageAdapter = new GlobalMessageAdapter(this, Connection.database, id, messageCursor, linearLayoutManager);
            recyclerView.setAdapter(globalMessageAdapter);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.scrollToPosition(messageCursor.getCount() - 1);
            recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v,
                                           int left, int top, int right, int bottom,
                                           int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    //int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    int count = recyclerView.getAdapter().getItemCount() - 1;
                    if (lastPosition == count) {
                        try {
                            recyclerView.smoothScrollToPosition(lastPosition);
                            lastPosition = 0;
                        }catch (IllegalArgumentException e){
                            System.out.println(e);
                        }

                    }
                }
            });
        }else{
            recyclerView.setVisibility(View.INVISIBLE);
            noMessageImageView.setVisibility(View.VISIBLE);
            noMessageTextView.setVisibility(View.VISIBLE);
        }


    }

    private Cursor getAllMessage(){
        return Connection.database.getAllGlobalMsg();
    }

    private void setBackgroundImage(){
        Cursor c=Connection.database.getBackgroundImage();
        if(c==null||c.getCount()==0)return;
        c.moveToLast();
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(0));
        Drawable draw = new BitmapDrawable(getResources(), bitmap);
        chatBackground.setBackground(draw);
        c.close();
    }
}