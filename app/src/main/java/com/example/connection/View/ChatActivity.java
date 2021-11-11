package com.example.connection.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.MessageController;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.R;


public class ChatActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ChatController chatController = ChatController.getInstance();
    private EditText message_input;
    private ImageView sendView;
    private RecyclerView recyclerView;
    private MessageAdapter chatAdapter;
    private ConstraintLayout chatBackground;
    private Toolbar toolbar;
    private User user;
    private int lastPosition;
    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private Context context;
    private Database database;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new Database(this);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.lyt_chat_activity);
        context = this;
        user = database.getUser(getIntent().getStringExtra("idChat"));
        Connection.idChatOpen = user.getIdUser();
        String username = getIntent().getStringExtra("username");
        if (MessageController.getIstance().getMessagingStyleHashMap().get(user.getIdUser()) != null) {
            MessageController.getIstance().getMessagingStyleHashMap().replace(user.getIdUser(), new NotificationCompat.MessagingStyle(username));
        }
        MessageController.getIstance().getMessagingStyleHashMap();
        TextView nameTextView = findViewById(R.id.nameUser);
        nameTextView.setText(username);
        toolbar = findViewById(R.id.toolbar2);
        createAlertDialogChatInformation(toolbar, user);
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
        message_input.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                } catch (IllegalArgumentException e) {
                    System.out.println("errore nella chat; solitamente vuota");
                }
                return false;
            }
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

        setupRecyclerView();

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatController.sendTCPMsg(message_input.getText().toString(), user.getIdUser());
                MessageController.getIstance().setMessageAdapter(chatAdapter);
            }
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
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new MessageAdapter(this, Connection.database, user.getIdUser(), messageCursor, linearLayoutManager);
        recyclerView.setAdapter(chatAdapter);
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
                    } catch (IllegalArgumentException e) {
                        System.out.println("Errore nella chat solitamente nulla");
                    }
                    lastPosition = 0;
                }
            }
        });
    }

    private Cursor getAllMessage() {
        return Connection.database.getAllMsg(user.getIdUser());
    }

    private void setBackgroundImage() {
        Cursor c = Connection.database.getBackgroundImage();
        if (c == null || c.getCount() == 0) return;
        c.moveToLast();
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(0));
        Drawable draw = new BitmapDrawable(getResources(), bitmap);
        chatBackground.setBackground(draw);
        c.close();
    }

    private void createAlertDialogChatInformation(Toolbar toolbar, User user) {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_chat_information);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();
                TextView usernameTextView = alertDialog.findViewById(R.id.dialogChatInformationUsernameTextView);
                TextView ageAndGenderTextView = alertDialog.findViewById(R.id.dialogChatInformationAgeAndGenderTextView);
                TextView textViewChatterNumber = alertDialog.findViewById(R.id.dialogChatInformationChatterNumberTextView);
                TextView textViewChatterWhatsapp = alertDialog.findViewById(R.id.dialogChatInformationChatterWhatsappTextView);
                TextView textViewChatterTelegram = alertDialog.findViewById(R.id.dialogChatInformationChatterTelegramTextView);

                isSomethingShared(textViewChatterNumber, textViewChatterWhatsapp, textViewChatterTelegram);

                usernameTextView.setText(user.getUsername());
                String ageAndGender = user.getAge() + ", " + user.getGender();
                ageAndGenderTextView.setText(ageAndGender);

                LinearLayout chatterNumber = alertDialog.findViewById(R.id.linearLayoutChatterNumber);
                LinearLayout chatterWhatsappNumber = alertDialog.findViewById(R.id.linearLayoutChatterWhatsapp);
                LinearLayout chatterTelegramNumber = alertDialog.findViewById(R.id.linearLayoutChatterTelegram);

                chatterNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showChatterNumber();
                    }
                });

                chatterWhatsappNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChatterWhatsapp();
                    }
                });

                chatterTelegramNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openChatterTelegram();
                    }
                });

                LinearLayout number = alertDialog.findViewById(R.id.linearLayoutNumber);
                LinearLayout whatsappNumber = alertDialog.findViewById(R.id.linearLayoutWhatsapp);
                LinearLayout telegramNumber = alertDialog.findViewById(R.id.linearLayoutTelegram);

                number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            chatController.share(database.getNumber(ConnectionController.myUser.getIdUser()) + "£€number", user.getIdUser());
                        } catch (NullPointerException e) {
                            System.out.println("account disconnected");
                        }
                    }
                });

                whatsappNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            chatController.share(database.getNumber(ConnectionController.myUser.getIdUser()) + "£€whatsapp", user.getIdUser());
                        } catch (NullPointerException e) {
                            System.out.println("account disconnected");
                        }
                    }
                });

                telegramNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            chatController.share(database.getTelegramNick(ConnectionController.myUser.getIdUser()) + "£€telegram", user.getIdUser());
                        } catch (NullPointerException e) {
                            System.out.println("account disconnected");
                        }
                    }
                });

                alertDialog.findViewById(R.id.closeTextView).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void isSomethingShared(TextView number, TextView whatsapp, TextView telegram) {
        if (database.isNumberShared(user.getIdUser(), "number")) {
            number.setText(user.getNumber());
        }
        if (database.isNumberShared(user.getIdUser(), "whatsapp")) {
            whatsapp.setText(user.getNumber());
        }
        if (database.isNumberShared(user.getIdUser(), "telegram")) {
            telegram.setText(database.getTelegramNick(user.getIdUser()));
        }
    }

    private void showChatterNumber() {
        if (database.isNumberShared(user.getIdUser(), "number")) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("number", user.getNumber());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Number copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            if (user.getGender().equals("male"))
                Toast.makeText(context, "The user has not shared his number", Toast.LENGTH_SHORT).show();
            else if (user.getGender().equals("female"))
                Toast.makeText(context, "The user has not shared her number", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "The user has not shared his/her number", Toast.LENGTH_SHORT).show();
        }
    }

    private void openChatterWhatsapp() {
        if (database.isNumberShared(user.getIdUser(), "whatsapp")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + user.getNumber())));
        } else {
            if (user.getGender().equals("male"))
                Toast.makeText(context, "The user has not shared his whatsapp", Toast.LENGTH_SHORT).show();
            else if (user.getGender().equals("female"))
                Toast.makeText(context, "The user has not shared her whatsapp", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "The user has not shared his/her whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    private void openChatterTelegram() {
        if (database.isNumberShared(user.getIdUser(), "telegram")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/" + database.getTelegramNick(user.getIdUser()))));
        } else {
            if (user.getGender().equals("male"))
                Toast.makeText(context, "The user has not shared his telegram", Toast.LENGTH_SHORT).show();
            else if (user.getGender().equals("female"))
                Toast.makeText(context, "The user has not shared her telegram", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "The user has not shared his/her telegram", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Connection.idChatOpen = "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Connection.idChatOpen = "";
    }

    @Override
    protected void onPause() {
        super.onPause();
        Connection.idChatOpen = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        Connection.idChatOpen = user.getIdUser();
    }
}