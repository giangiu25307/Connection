package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.GlobalMessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Listener.MessageListener;
import com.example.connection.Model.Message;
import com.example.connection.R;
import com.example.connection.util.RecyclerItemClickListener;

import java.util.ArrayList;

public class ChatGlobalActivity extends AppCompatActivity {

    private Context context;
    private SharedPreferences sharedPreferences;
    private ChatController chatController = ChatController.getInstance();
    private String id;
    private TextView noMessageTextView;
    private EditText messageInput;
    private ImageButton sendView;
    private ImageView noMessageImageView;
    private RecyclerView recyclerView;
    private GlobalMessageAdapter globalMessageAdapter;
    private ConstraintLayout chatBackground;
    private int lastPosition;
    private final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
    private ArrayList<Message> messageList = new ArrayList<>();
    private Toolbar toolbar;
    ActionMode actionMode;
    Menu contextMenu;
    boolean isMultiSelect = false;
    String idSelectedMessage = "";
    int selectedMessagePosition = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        loadTheme();
        setContentView(R.layout.lyt_chat_global);
        context = this;
        toolbar = findViewById(R.id.toolbar2);
        ImageView imageView = findViewById(R.id.backImageView);
        imageView.setOnClickListener(view -> finish());
        messageInput = findViewById(R.id.message_input);
        sendView = findViewById(R.id.sendView);
        chatBackground = findViewById(R.id.chatBackground);
        messageInput.setOnTouchListener((view, event) -> {
            try {
                lastPosition = linearLayoutManager.findLastVisibleItemPosition();
            } catch (IllegalArgumentException e) {
                System.out.println(e);
            }
            return false;
        });
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (messageInput.getText().toString().replace(" ", "").isEmpty()) {
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
            chatController.sendGlobalMsg(messageInput.getText().toString());
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
        Cursor messageCursor = Connection.database.getAllGlobalMsg();
        recyclerView = findViewById(R.id.messageRecyclerView);
        globalMessageAdapter = new GlobalMessageAdapter(this, Connection.database, id, messageList, linearLayoutManager, recyclerView, noMessageImageView, noMessageTextView);
        MessageListener.getIstance().setGlobalMessageAdapter(globalMessageAdapter);
        recyclerView.setAdapter(globalMessageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        setBackgroundImage();
        if (messageCursor != null && messageCursor.getCount() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageImageView.setVisibility(View.INVISIBLE);
            noMessageTextView.setVisibility(View.INVISIBLE);
            fillMessagesArrayList(messageCursor);
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
                            System.out.println(e);
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


    }

    private void fillMessagesArrayList(Cursor messageCursor) {
        do {
            messageList.add(new Message(messageCursor.getString(0), messageCursor.getString(1), messageCursor.getString(2), messageCursor.getString(3), "", messageCursor.getString(4)));
        } while (messageCursor.moveToNext());
    }

    private void defineRecyclerViewOnLong(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    selectedMessagePosition = position;
                    manageSelectedMessage(recyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = toolbar.startActionMode(actionModeCallback);
                        actionMode.setTitle("1 selected");
                    }
                }
                selectedMessagePosition = position;
                manageSelectedMessage(recyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString());
            }
        }));
    }

    public void manageSelectedMessage(String id) {

        if (actionMode != null) {

            if (idSelectedMessage.equals(id)) {
                refreshAdapter();
                actionMode.finish();
            } else {
                refreshAdapter();
                idSelectedMessage = id;
                refreshAdapter();
            }

        }

    }

    public void refreshAdapter() {
        globalMessageAdapter.idSelectedMessage = idSelectedMessage;
        globalMessageAdapter.notifyItemChanged(selectedMessagePosition);
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.global_chat_menu, menu);
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
                    String id = idSelectedMessage;
                    for (Message message : messageList) {
                        if (message.getIdMessage().equals(id)) {
                            ClipData clip = ClipData.newPlainText("message", message.getMessage());
                            clipboard.setPrimaryClip(clip);
                        }
                    }
                    Toast.makeText(context, "Message copied to clipboard", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            isMultiSelect = false;
            idSelectedMessage = "";
            refreshAdapter();
        }
    };

    private void setBackgroundImage() {
        Cursor c = Connection.database.getBackgroundImage();
        if (c == null || c.getCount() == 0) return;
        c.moveToLast();
        Bitmap bitmap = BitmapFactory.decodeFile(c.getString(0));
        Drawable draw = new BitmapDrawable(getResources(), bitmap);
        chatBackground.setBackground(draw);
        c.close();
    }
}