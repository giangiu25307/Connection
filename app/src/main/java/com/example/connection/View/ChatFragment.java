package com.example.connection.View;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.RequestAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.Database;
import com.example.connection.R;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private Database database;
    private Button requestButton;
    private ChatController chatController;
    private TextView totalChat;
    private Toolbar toolbar;

    public ChatFragment() {
    }

    public ChatFragment newInstance(Database database,ChatController chatController, Toolbar toolbar) {
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setDatabase(database);
        chatFragment.setChatController(chatController);
        chatFragment.setToolbar(toolbar);
        return chatFragment;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.chat_fragment, null);

        setHasOptionsMenu(true);

        /*
        sharedPreferences = getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("appTheme", "light").equals("light")){
            textColor = Color.BLACK;
        }else{
            textColor = Color.WHITE;
        }

        globalButton = view.findViewById(R.id.globalButton);
        globalButton.setOnClickListener(this);


        TextView textView = view.findViewById(R.id.requestTextView);
        textView.setText("1");
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.left_to_right);
        textView.startAnimation(animation);
        */
        requestButton = view.findViewById(R.id.requestLinearLayout);
        int totalRequest = database.getAllRequestChat().getCount();
        requestButton.setText(totalRequest == 1 ? totalRequest + " request" : totalRequest + " requests");//(totalRequest==0 ? "No" : ""+totalRequest);
        totalChat = toolbar.findViewById(R.id.toolbarTitle);
        int totalChatNumber = database.getAllNoRequestChat().getCount();
        totalChat.setText(totalChatNumber == 0 ? "Chat (0)" : "Chat (" + totalChatNumber + ")");
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                crossToRequestDialog(dialogBuilder);
            }
        });
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view){
        RecyclerView recyclerView = view.findViewById(R.id.chatRecyclerView);
        System.out.println(database);
        Cursor cursor = database.getAllNoRequestChat();
        ChatAdapter chatAdapter = new ChatAdapter(getContext(), cursor, database, chatController);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if(cursor.getCount() == 0){
            TextView textView = view.findViewById(R.id.textView0Chat);
            textView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private void crossToRequestDialog(AlertDialog.Builder dialogBuilder){
        dialogBuilder.setView(R.layout.dialog_request);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.findViewById(R.id.closeRequestDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        setupRequestRecyclerView(alertDialog);

    }

    private void setupRequestRecyclerView(AlertDialog view){
        RecyclerView recyclerView = view.findViewById(R.id.requestRecycleView);
        //System.out.println(database);
        Cursor cursor = database.getAllRequestChat();
        RequestAdapter requestAdapter = new RequestAdapter(getContext(), cursor, database, chatController, requestButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(requestAdapter);
        /*if(cursor.getCount() == 0){                                    //THERE IS NOTHING HERE
            TextView textView = view.findViewById(R.id.textView0Chat);
            textView.setVisibility(View.VISIBLE);
        }*/
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.globalIcon:
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }
}
