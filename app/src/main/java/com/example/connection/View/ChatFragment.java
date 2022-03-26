package com.example.connection.View;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Adapter.RequestAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Listener.MessageListener;
import com.example.connection.Database.Database;
import com.example.connection.Model.Chat;
import com.example.connection.R;
import com.example.connection.util.RecyclerItemClickListener;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Objects;

public class ChatFragment extends Fragment implements View.OnClickListener {

    private Database database;
    private Button requestButton;
    private ChatController chatController;
    private TextView totalChat, noChatTextView;
    private Toolbar toolbar;
    private Cursor chatCursor;
    private ChatAdapter chatAdapter;
    private RecyclerView chatRecyclerView;
    private ImageView noChatImageView;
    private static ChatFragment chatFragment;
    private AlertDialog requestAlertDialog;
    private int position;

    private int totalChatNumber, totalRequest;

    ActionMode actionMode;
    Menu contextMenu;
    boolean isMultiSelect = false;
    ArrayList<Chat> chatsList = new ArrayList<>();
    ArrayList<String> multiselectList = new ArrayList<>();

    public ChatFragment() {

    }


    public ChatFragment newInstance(Database database, ChatController chatController, Toolbar toolbar) {
        chatFragment = new ChatFragment();
        chatFragment.setDatabase(database);
        chatFragment.setChatController(chatController);
        chatFragment.setToolbar(toolbar);
        return chatFragment;
    }

    public static ChatFragment getIstance() {
        return chatFragment;
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    private void setRequestAlertDialog(AlertDialog requestAlertDialog) {
        this.requestAlertDialog = requestAlertDialog;
    }

    public AlertDialog getRequestAlertDialog() {
        return requestAlertDialog;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        @SuppressLint("inflateParams") View view = inflater.inflate(R.layout.lyt_chat_fragment, null);

        setHasOptionsMenu(true);

        chatFragment.requestButton = view.findViewById(R.id.requestButton);
        chatFragment.setTotalRequest(chatFragment.database.getAllRequestChat().getCount());
        chatFragment.setTotalChatNumber(chatFragment.database.getAllNoRequestChat().getCount());
        chatFragment.requestButton.setText(totalRequest <= 1 ? totalRequest + " request" : totalRequest + " requests");//(totalRequest==0 ? "No" : ""+totalRequest);
        chatFragment.totalChat = chatFragment.toolbar.findViewById(R.id.toolbarTitle);
        chatFragment.requestButton.setOnClickListener(view1 -> {
            Connection.isRequestDialogOpen = true;
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(), R.style.CustomAlertDialog);
            crossToRequestDialog(dialogBuilder);
        });

        chatFragment.noChatImageView = view.findViewById(R.id.noChatImageView);
        chatFragment.noChatTextView = view.findViewById(R.id.noChatTextView);

        setupRecyclerView(view);
        return view;
    }

    public void setupRecyclerView(View view) {
        if (chatAdapter != null) position = chatAdapter.getPosition();
        chatFragment.totalChat.setText(chatFragment.getTotalChatNumber() <= 1 ? "Chat (" + chatFragment.getTotalChatNumber() + ")" : "Chats (" + chatFragment.getTotalChatNumber() + ")");
        chatFragment.requestButton.setText(chatFragment.getTotalRequest() <= 1 ? chatFragment.getTotalRequest() + " request" : chatFragment.getTotalRequest() + " requests");
        chatsList.clear();
        chatRecyclerView = view.findViewById(R.id.chatRecyclerView);
        chatCursor = chatFragment.database.getAllNoRequestChat();
        if (chatCursor != null && chatCursor.getCount() > 0) {
            fillChatArrayList();
            chatAdapter = new ChatAdapter(getContext(), chatsList, chatFragment.database, chatFragment.chatController);
            chatRecyclerView.setAdapter(chatAdapter);
            chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            defineRecyclerViewOnLong(chatRecyclerView);
            MessageListener.getIstance().setChatAdapter(chatAdapter);
            chatRecyclerView.scrollToPosition(position);
            chatRecyclerView.setVisibility(View.VISIBLE);
            noChatImageView.setVisibility(View.INVISIBLE);
            noChatTextView.setVisibility(View.INVISIBLE);
        } else {
            chatRecyclerView.setVisibility(View.INVISIBLE);
            noChatImageView.setVisibility(View.VISIBLE);
            noChatTextView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    private void crossToRequestDialog(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setView(R.layout.dialog_request);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
        alertDialog.findViewById(R.id.closeRequestDialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Connection.isRequestDialogOpen = true;
            }
        });
        chatFragment.setRequestAlertDialog(alertDialog);
        setupRequestRecyclerView();

    }

    public static void setupRequestRecyclerView() {
        chatFragment.requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog view = chatFragment.getRequestAlertDialog();
                int totalRequest = chatFragment.getTotalRequest();
                TextView requestTextView = view.findViewById(R.id.requestTextView);
                requestTextView.setText(totalRequest <= 1 ? " Request (" + totalRequest + ")" : " Requests (" + totalRequest + ")");
                chatFragment.requestButton.setText(chatFragment.getTotalRequest() <= 1 ? chatFragment.getTotalRequest() + " request" : chatFragment.getTotalRequest() + " requests");
                RecyclerView recyclerView = view.findViewById(R.id.requestRecycleView);
                ImageView noRequestImageView = view.findViewById(R.id.noRequestImageView);
                TextView noRequestTextView = view.findViewById(R.id.noRequestTextView);
                Cursor cursor = chatFragment.database.getAllRequestChat();
                if (cursor != null && cursor.getCount() > 0) {
                    RequestAdapter requestAdapter = new RequestAdapter(chatFragment.getContext(), cursor, chatFragment.database, chatFragment.chatController, chatFragment.requestButton, requestTextView);
                    if (recyclerView != null) {
                        recyclerView.setAdapter(requestAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(chatFragment.getContext()));
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    if (noRequestImageView != null) {
                        noRequestImageView.setVisibility(View.INVISIBLE);
                    }
                    if (noRequestTextView != null) {
                        noRequestTextView.setVisibility(View.INVISIBLE);
                    }
                } else {
                    if (recyclerView != null) {
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    if (noRequestImageView != null) {
                        noRequestImageView.setVisibility(View.VISIBLE);
                    }
                    if (noRequestTextView != null) {
                        noRequestTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void defineRecyclerViewOnLong(RecyclerView recyclerView) {
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect) {
                    addRemoveMultiSelect(chatRecyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString(), position);
                } else {
                    final String id = chatsList.get(position).getId();
                    Intent myIntent = new Intent(getContext(), ChatActivity.class);
                    // myIntent.putExtra("chatFragment.chatController", chatFragment.chatController); //Optional parameters\
                    myIntent.putExtra("idChat", id);
                    //TODO Da cambiare in username
                    myIntent.putExtra("username", chatsList.get(position).getName());
                    getContext().startActivity(myIntent);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    multiselectList = new ArrayList<>();
                    isMultiSelect = true;

                    if (actionMode == null) {
                        actionMode = chatFragment.toolbar.startActionMode(actionModeCallback);
                    }
                }
                addRemoveMultiSelect(chatRecyclerView.findViewHolderForAdapterPosition(position).itemView.getTag().toString(), position);
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
            actionMode.setTitle(multiselectList.size() > 0 ? multiselectList.size() + " selected" : "0");
            refreshAdapter(position, false);
        }

    }

    public void refreshAdapter(int position, boolean destroyedActionMode) {
        chatAdapter.selectedChat = multiselectList;
        if (destroyedActionMode) {
            chatAdapter.notifyDataSetChanged();
        } else {
            chatAdapter.notifyItemChanged(position);
        }
        MessageListener.getIstance().setChatAdapter(chatAdapter);
    }

    private void fillChatArrayList() {
        chatCursor.moveToFirst();
        do {
            chatsList.add(new Chat(chatCursor.getString(0), chatCursor.getString(1), chatCursor.getString(2), chatCursor.getString(3)));
        } while (chatCursor.moveToNext());
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

    public int getTotalChatNumber() {
        return totalChatNumber;
    }

    public void setTotalChatNumber(int totalChatNumber) {
        this.totalChatNumber = totalChatNumber;
    }

    public int getTotalRequest() {
        return totalRequest;
    }

    public void setTotalRequest(int totalRequest) {
        this.totalRequest = totalRequest;
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.chats_action_menu, menu);
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
                    TextView titleTextView = alertDialog.findViewById(R.id.deleteDialogTitle);
                    titleTextView.setText(chatSelected > 1 ? "Delete chats" : "Delete chat");
                    TextView subtitleTextView = alertDialog.findViewById(R.id.deleteDialogSubtitle);
                    subtitleTextView.setText(chatSelected > 1 ? "Are you sure you want to delete " + chatSelected + " chats? The operation cannot be undone"
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
            multiselectList.clear();
            refreshAdapter(0, true);
        }
    };

    private void deleteSelectedChat() {
        //Collections.sort(multiselectList, Collections.reverseOrder());

        for (String id : multiselectList) {
            chatFragment.database.deleteChat(id);
            chatAdapter.removeChat(id);
            chatsList.removeIf(chat -> chat.getId().equals(id));
        }

        chatFragment.setTotalChatNumber(chatFragment.getTotalChatNumber() - multiselectList.size());
        chatFragment.totalChat.setText(chatFragment.getTotalChatNumber() <= 1 ? "Chat (" + chatFragment.getTotalChatNumber() + ")" : "Chats (" + chatFragment.getTotalChatNumber() + ")");

        String snackbarText = multiselectList.size() > 1 ? multiselectList.size() + " chats deleted" : "1 chat deleted";

        Snackbar snackbar = Snackbar.make(getView(), "", Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
        layout.setBackgroundColor(getContext().getColor(R.color.transparent));
        TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        if(chatsList.size() == 0){
            chatFragment.chatRecyclerView.setVisibility(View.VISIBLE);
            chatFragment.noChatImageView.setVisibility(View.INVISIBLE);
            chatFragment.noChatTextView.setVisibility(View.INVISIBLE);
        }

        View snackView = getActivity().getLayoutInflater().inflate(R.layout.lyt_chats_messages_deleted_snackbar, null);
        TextView textView1 = snackView.findViewById(R.id.textView);
        textView1.setText(snackbarText);
        layout.setPadding(5, 5, 5, 5);
        layout.addView(snackView, 0);
        snackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Connection.amIComingFromChatActivity && Connection.isNewMessageArrived){
            setupRecyclerView(chatFragment.getView());
            if(Connection.isRequestDialogOpen){
                setupRequestRecyclerView();
            }
        }
        Connection.amIComingFromChatActivity = false;
        Connection.isNewMessageArrived = false;
        try {
            chatAdapter.swapCursor(database.getAllNoRequestChat());
            chatRecyclerView.scrollToPosition(position);
        } catch (NullPointerException e) {
            System.out.println("[Message Information Chat Fragment] cursore vuoto");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            position = chatAdapter.getPosition();
        } catch (NullPointerException e) {
            System.out.println("[Message Information Chat Fragment] cursore vuoto");
        }
    }
}
