package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Settings;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.connection.Controller.MessageController;
import com.example.connection.Database.Database;
import com.example.connection.Model.Chat;
import com.example.connection.R;
import com.example.connection.util.RecyclerItemClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private Database database;
    private Button requestButton;
    private ChatController chatController;
    private TextView totalChat;
    private Toolbar toolbar;
    private Cursor chatCursor;
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;

    ActionMode actionMode;
    Menu contextMenu;
    boolean isMultiSelect = false;
    ArrayList<Chat> chatsList = new ArrayList<>();
    ArrayList<String> multiselectList = new ArrayList<>();

    public ChatFragment() {
    }

    public ChatFragment newInstance(Database database, ChatController chatController, Toolbar toolbar) {
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
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_chat_fragment, null);

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
        requestButton = view.findViewById(R.id.requestButton);
        int totalRequest = database.getAllRequestChat().getCount();
        requestButton.setText(totalRequest <= 1 ? totalRequest + " request" : totalRequest + " requests");//(totalRequest==0 ? "No" : ""+totalRequest);
        totalChat = toolbar.findViewById(R.id.toolbarTitle);
        int totalChatNumber = database.getAllNoRequestChat().getCount();
        totalChat.setText(totalChatNumber <= 1 ? "Chat (" + totalChatNumber + ")" : "Chats (" + totalChatNumber + ")");
        requestButton.setOnClickListener(view1 -> {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
            crossToRequestDialog(dialogBuilder, totalRequest);
        });
        setupRecyclerView(view);
        return view;
    }

    private void setupRecyclerView(View view) {
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatCursor = database.getAllNoRequestChat();
        if(chatCursor != null){
            fillChatArrayList();
        }else{
            //Gestire il caso in cui non ci siano chat
        }

        chatAdapter = new ChatAdapter(getContext(), chatsList, database, chatController);
        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        /*if(cursor.getCount() == 0){
            TextView textView = view.findViewById(R.id.textViewChat);
            textView.setVisibility(View.VISIBLE);
        }*/
        defineRecyclerViewOnLong(chatRecyclerView);
        MessageController.getIstance().setChatAdapter(chatAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private void crossToRequestDialog(AlertDialog.Builder dialogBuilder, int totalRequest) {
        dialogBuilder.setView(R.layout.dialog_request);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        TextView requestTextView = alertDialog.findViewById(R.id.requestTextView);
        requestTextView.setText(totalRequest <= 1 ? " Request (" + totalRequest + ")" : " Requests (" + totalRequest + ")");
        alertDialog.findViewById(R.id.closeRequestDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        setupRequestRecyclerView(alertDialog);

    }

    private void setupRequestRecyclerView(AlertDialog view) {
        RecyclerView recyclerView = view.findViewById(R.id.requestRecycleView);
        Cursor cursor = database.getAllRequestChat();
        RequestAdapter requestAdapter = new RequestAdapter(getContext(), cursor, database, chatController, requestButton);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(requestAdapter);
        /*if(cursor.getCount() == 0){                                    //THERE IS NOTHING HERE
            TextView textView = view.findViewById(R.id.textView0Chat);
            textView.setVisibility(View.VISIBLE);
        }*/
    }

    private void defineRecyclerViewOnLong(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (isMultiSelect) {
                            addRemoveMultiSelect(chatRecyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString());
                        }else{
                            final String id = chatsList.get(position).getId();
                            Intent myIntent = new Intent(getContext(), ChatActivity.class);
                            // myIntent.putExtra("chatController", chatController); //Optional parameters\
                            myIntent.putExtra("idChat", id);
                            //TODO Da cambiare in username
                            myIntent.putExtra("username", chatsList.get(position).getName());
                            getContext().startActivity(myIntent);
                        }
                        System.out.println("Andato da fragment");
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                        System.out.println("Multi select" + isMultiSelect);
                        if (!isMultiSelect) {
                            multiselectList = new ArrayList<>();
                            isMultiSelect = true;

                            if (actionMode == null) {
                                actionMode = toolbar.startActionMode(actionModeCallback);
                            }
                        }
                        System.out.println("Position " + position);
                        addRemoveMultiSelect(chatRecyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString());
                    }
                }));


    }

    public void addRemoveMultiSelect(String position) {

        if (actionMode != null) {
            if (multiselectList.contains(position)) {
                multiselectList.remove(position);
            } else {
                multiselectList.add(position);
            }

            actionMode.setTitle(multiselectList.size() > 0 ? "" + multiselectList.size() + " selected" : "0");
            refreshAdapter();
        }

    }

    public void refreshAdapter() {
        chatAdapter.selectedUsersList = multiselectList;
        chatAdapter.notifyDataSetChanged();
        MessageController.getIstance().setChatAdapter(chatAdapter);
    }

    private void fillChatArrayList(){
        chatCursor.moveToFirst();
        do{
            chatsList.add(new Chat(chatCursor.getString(0), chatCursor.getString(1), chatCursor.getString(2), chatCursor.getString(3)));
        }while(chatCursor.moveToNext());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.chat_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.globalIcon) {
            Intent intent = new Intent(getContext(), ChatGlobalActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
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
                case R.id.deleteIcon:
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
                    dialogBuilder.setView(R.layout.dialog_confirm_delete_chat_message);
                    final AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                    int chatSelected = multiselectList.size();
                    TextView titletextView = alertDialog.findViewById(R.id.deleteDialogTitle);
                    titletextView.setText(chatSelected > 1 ? "Delete chats?" : "Delete chat?");
                    TextView subtitleTextView = alertDialog.findViewById(R.id.deleteDialogSubtitle);
                    subtitleTextView.setText(chatSelected > 1 ? "Are you sure you want to delete " + chatSelected +" chats? The operation cannot be undone"
                            : "Are you sure you want to delete " + chatSelected + " chat? The operation cannot be undone");
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
                            deleteSelectedChat();
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
            multiselectList = new ArrayList<>();
            refreshAdapter();
        }
    };

    private void deleteSelectedChat(){
        //Collections.sort(multiselectList, Collections.reverseOrder());

        for (String id: multiselectList) {
            database.deleteChat(id);
            chatAdapter.removeChat(id);
            chatsList.removeIf(chat -> chat.getId().equals(id));
        }

        String snackbarText = multiselectList.size() > 1 ? multiselectList.size() + " chats deleted" : "1 chat deleted";

        Snackbar snackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getContext().getColor(R.color.transparent));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        View snackView = getActivity().getLayoutInflater().inflate(R.layout.lyt_chats_deleted_snackbar, null);
        TextView textView1 = snackView.findViewById(R.id.textView);
        textView1.setText(snackbarText);
        layout.setPadding(5, 5, 5, 5);
        layout.addView(snackView, 0);
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(actionMode != null){
            actionMode.finish();
        }
    }
}
