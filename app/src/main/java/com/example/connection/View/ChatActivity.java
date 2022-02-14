package com.example.connection.View;

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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.Listener.MessageListener;
import com.example.connection.Database.Database;
import com.example.connection.Model.Message;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.util.RecyclerItemClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;


public class ChatActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ChatController chatController = ChatController.getInstance();
    private EditText message_input;
    private ImageView noMessageImageView;
    private ImageButton sendView;
    private TextView noMessageTextView;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private ConstraintLayout chatBackground;
    private Toolbar toolbar;
    private User user;
    private int lastPosition;
    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private Context context;
    private Database database;
    private String idChat;
    ActionMode actionMode;
    Menu contextMenu;
    boolean isMultiSelect = false;
    private ArrayList<Message> messageList = new ArrayList<>();
    ArrayList<String> multiselectList = new ArrayList<>();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = new Database(this);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.lyt_chat_activity);
        context = this;
        idChat = getIntent().getStringExtra("idChat");
        database.setReadAllMessages(idChat);
        user = database.getUser(idChat);
        Connection.idChatOpen = user.getIdUser();
        String username = getIntent().getStringExtra("username");
        if (MessageListener.getIstance().getMessagingStyleHashMap().get(user.getIdUser()) != null) {
            MessageListener.getIstance().getMessagingStyleHashMap().replace(user.getIdUser(), new NotificationCompat.MessagingStyle(username));
        }
        MessageListener.getIstance().getMessagingStyleHashMap();
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
        ImageView profilePicImageView = findViewById(R.id.profilePic);
        File profilePic = new File(user.getProfilePic());
        if (profilePic.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
            profilePicImageView.setImageBitmap(myBitmap);
        }
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

        noMessageImageView = findViewById(R.id.noMessageImageView);
        noMessageTextView = findViewById(R.id.noMessageTextView);
        setupRecyclerView();

        sendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatController.sendTCPMsg(message_input.getText().toString(), user.getIdUser());
                MessageListener.getIstance().setMessageAdapter(messageAdapter);
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
        recyclerView = findViewById(R.id.messageRecyclerView);
        Cursor messageCursor = Connection.database.getAllMsg(user.getIdUser());
        setBackgroundImage();
        if (messageCursor != null && messageCursor.getCount() > 0) {
            fillMessagesArrayList(messageCursor);
            messageAdapter = new MessageAdapter(this, Connection.database, user.getIdUser(), messageList, linearLayoutManager);
            recyclerView.setAdapter(messageAdapter);
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
                        } catch (IllegalArgumentException e) {
                            System.out.println("Errore nella chat solitamente nulla");
                        }
                        lastPosition = 0;
                    }
                }
            });
            defineRecyclerViewOnLong(recyclerView);
        } else {
            recyclerView.setVisibility(View.INVISIBLE);
            noMessageImageView.setVisibility(View.VISIBLE);
            noMessageTextView.setVisibility(View.VISIBLE);
        }
        //Esempio di come aggiungere un messaggio a runtime
        //database.addMsg("Messaggio 7", "0", idChat);
        //messageAdapter.addMessage(new Message(database.getLastMessageId(), "0", "Messaggio 7", "2021-12-10T23:33:34.939"));
    }

    private void defineRecyclerViewOnLong(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    addRemoveMultiSelect(recyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString(), position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselectList = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = toolbar.startActionMode(actionModeCallback);
                    }
                }
                addRemoveMultiSelect(recyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString(), position);
            }
        }));
    }

    public void addRemoveMultiSelect(String id, int position) {

        if (actionMode != null) {

            if (multiselectList.contains(id)) {
                multiselectList.remove(id);
            } else {
                multiselectList.add(id);
            }

            switch (multiselectList.size()) {
                case 0:
                    actionMode.finish();
                    break;
                case 1:
                    actionMode.setTitle("1 selected");
                    refreshAdapter(position, false);
                    if (!contextMenu.findItem(R.id.copyIcon).isVisible())
                        contextMenu.findItem(R.id.copyIcon).setVisible(true);
                    break;
                default:
                    actionMode.setTitle(multiselectList.size() + " selected");
                    refreshAdapter(position, false);
                    if (contextMenu.findItem(R.id.copyIcon).isVisible())
                        contextMenu.findItem(R.id.copyIcon).setVisible(false);
                    break;
            }

        }

    }

    public void refreshAdapter(int position, boolean destroyedActionMode) {
        messageAdapter.selectedMessage = multiselectList;
        if (destroyedActionMode) {
            messageAdapter.notifyDataSetChanged();
        } else {
            messageAdapter.notifyItemChanged(position);
        }
    }

    private void fillMessagesArrayList(Cursor messageCursor) {
        do {
            messageList.add(new Message(messageCursor.getString(0), messageCursor.getString(1), messageCursor.getString(2), messageCursor.getString(4)));
        } while (messageCursor.moveToNext());
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

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.chat_action_menu, menu);
            contextMenu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.copyIcon:
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    String id = multiselectList.get(0);
                    for (Message message : messageList) {
                        if (message.getIdMessage().equals(id)) {
                            ClipData clip = ClipData.newPlainText("message", message.getMessage());
                            clipboard.setPrimaryClip(clip);
                        }
                    }
                    Toast.makeText(context, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                case R.id.deleteIcon:
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChatActivity.this, R.style.CustomAlertDialog);
                    dialogBuilder.setView(R.layout.dialog_confirm_delete_chat_message);
                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                    int chatSelected = multiselectList.size();
                    TextView titleTextView = alertDialog.findViewById(R.id.deleteDialogTitle);
                    titleTextView.setText(chatSelected > 1 ? "Delete messages" : "Delete message");
                    TextView subtitleTextView = alertDialog.findViewById(R.id.deleteDialogSubtitle);
                    subtitleTextView.setText(chatSelected > 1 ? "Are you sure you want to delete " + chatSelected + " messages? The operation cannot be undone"
                            : "Are you sure you want to delete " + chatSelected + " message? The operation cannot be undone");
                    alertDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                            deleteSelectedMessage();
                            actionMode.finish();
                        }
                    });
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            isMultiSelect = false;
            multiselectList.clear();
            refreshAdapter(0, true);
        }
    };

    private void deleteSelectedMessage() {

        for (String id : multiselectList) {
            database.deleteMessage(id, idChat);
            messageAdapter.removeMessage(id);
            messageList.removeIf(chat -> chat.getIdMessage().equals(id));
        }

        String snackbarText = multiselectList.size() > 1 ? multiselectList.size() + " messages deleted" : "1 message deleted";

        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().findViewById(R.id.lyt_chat_activity), "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(ChatActivity.this.getColor(R.color.transparent));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView = ChatActivity.this.getLayoutInflater().inflate(R.layout.lyt_chats_messages_deleted_snackbar, null);
        TextView textView1 = snackView.findViewById(R.id.textView);
        textView1.setText(snackbarText);
        layout.setPadding(5, 5, 5, 5);
        layout.addView(snackView, 0);
        snackbar.show();
    }


    private void createAlertDialogChatInformation(Toolbar toolbar, User user) {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                dialogBuilder.setView(R.layout.dialog_chat_information);
                final AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                ImageView profilePicImageView = findViewById(R.id.profilePicChatInformation);
                File profilePic = new File(user.getProfilePic());
                if (profilePic.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
                    profilePicImageView.setImageBitmap(myBitmap);
                }

                Button chatterNumber = alertDialog.findViewById(R.id.chatterNumberButton);
                Button chatterWhatsappNumber = alertDialog.findViewById(R.id.chatterWhatsappButton);
                Button chatterTelegramNumber = alertDialog.findViewById(R.id.chatterTelegramButton);

                TextView usernameTextView = alertDialog.findViewById(R.id.dialogChatInformationUsernameTextView);
                TextView ageAndGenderTextView = alertDialog.findViewById(R.id.dialogChatInformationAgeAndGenderTextView);

                isSomethingShared(chatterNumber, chatterWhatsappNumber, chatterTelegramNumber);

                usernameTextView.setText(user.getUsername());
                String ageAndGender = user.getAge() + ", " + user.getGender();
                ageAndGenderTextView.setText(ageAndGender);
                Button block = alertDialog.findViewById(R.id.blockButton);
                block.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.blockUser(user.getIdUser());
                    }
                });

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

                Button number = alertDialog.findViewById(R.id.userNumberButton);
                Button whatsappNumber = alertDialog.findViewById(R.id.userWhatsappButton);
                Button telegramNumber = alertDialog.findViewById(R.id.userTelegramButton);

                number.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            chatController.share(database.getNumber(ConnectionController.myUser.getIdUser()) + "£€number", user.getIdUser());
                            number.setText("Shared");
                        } catch (NullPointerException e) {
                            System.out.println("account disconnected");
                            number.setText("Not shared");
                        }
                    }
                });

                whatsappNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            chatController.share(database.getNumber(ConnectionController.myUser.getIdUser()) + "£€whatsapp", user.getIdUser());
                            whatsappNumber.setText("Shared");
                        } catch (NullPointerException e) {
                            System.out.println("account disconnected");
                            whatsappNumber.setText("Not shared");
                        }
                    }
                });

                telegramNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(database.getTelegramNick(ConnectionController.myUser.getIdUser()).equals("")){
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(alertDialog.getContext(), R.style.CustomAlertDialog);
                                dialogBuilder.setView(R.layout.dialog_telegram_username);
                                final AlertDialog alertDialog = dialogBuilder.create();
                                TextView telegramNick = alertDialog.findViewById(R.id.editTextTelegramNick);
                                Button confirm = alertDialog.findViewById(R.id.confirmTextView);
                                confirm.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        database.setTelegram(ConnectionController.myUser.getIdUser(),telegramNick.getText().toString());
                                        chatController.share(database.getTelegramNick(ConnectionController.myUser.getIdUser()) + "£€telegram", user.getIdUser());
                                        telegramNumber.setText("Shared");
                                        alertDialog.dismiss();
                                    }
                                });
                                Button cancel = alertDialog.findViewById(R.id.cancelTextView);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();
                                        telegramNumber.setText("Not shared");
                                    }
                                });
                            }else{
                                chatController.share(database.getTelegramNick(ConnectionController.myUser.getIdUser()) + "£€telegram", user.getIdUser());
                                telegramNumber.setText("Shared");
                            }
                        } catch (NullPointerException e) {
                            System.out.println("account disconnected");
                            telegramNumber.setText("Not shared");
                        }
                    }
                });

                alertDialog.findViewById(R.id.closeButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
            }
        });
    }

    private void isSomethingShared(Button number, Button whatsapp, Button telegram) {
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
        if (actionMode != null) {
            actionMode.finish();
        }
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