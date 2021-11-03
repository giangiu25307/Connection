package com.example.connection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Model.User;
import com.example.connection.View.ChatActivity;
import com.example.connection.Controller.ChatController;
import com.example.connection.Database.Database;
import com.example.connection.Controller.Task;
import com.example.connection.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private Cursor chatCursor;
    private User user;
    private Database database;
    private Bitmap bitmap, bitmap2;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    Date date = new Date();
    ChatController chatController;


    public ChatAdapter(Context context, Cursor chatCursor, Database database, ChatController chatController) {
        this.context = context;
        this.chatCursor = chatCursor;
        this.database = database;
        this.chatController = chatController;
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lyt_chat, parent, false);
        ViewHolder holder = new ViewHolder(view, new ViewHolder.OnChatClickListener() {

            @Override
            public void openChat(int p) {
                chatCursor.moveToPosition(p);
                final String id = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
                Intent myIntent = new Intent(context, ChatActivity.class);
                // myIntent.putExtra("chatController", chatController); //Optional parameters\
                myIntent.putExtra("idChat", id);
                //TODO Da cambiare in username
                myIntent.putExtra("username", chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.NAME)));
                context.startActivity(myIntent);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        if (!chatCursor.moveToPosition(position)) {
            return;
        }
        String id= chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
        user = database.getUser(id);
        //Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(userCursor));
        //Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));

        System.out.println(id);
        String lastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.LAST_MESSAGE));
        String datetime = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.DATETIME));
        File profilePic = new File(user.getProfilePic());

        if (profilePic.exists()) {

            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circle);
            bitmap2 = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
            BitmapDrawable layer1 = new BitmapDrawable(context.getResources(), bitmap2);
            BitmapDrawable layer2 = new BitmapDrawable(context.getResources(), bitmap);
            Drawable[] layers = {layer1, layer2};
            LayerDrawable layerDrawable = new LayerDrawable(layers);
            ImageView profilePicImageView = holder.profilePic;
            profilePicImageView.setImageTintList(null);
            profilePicImageView.setImageDrawable(layerDrawable);
            //bitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());

        }

        //String timeLastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.DATE));

        holder.itemView.setTag(id);

        TextView userNameTextView = holder.name;
        TextView lastMessageTextView = holder.lastMessage;
        TextView timeLastMessageTextView = holder.timeLastMessage;
        userNameTextView.setText(user.getUsername());
        lastMessageTextView.setText(lastMessage);
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = format.parse(String.valueOf(LocalDateTime.now()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (DateFormat.format("dd", date).equals(DateFormat.format("dd", date2)) && DateFormat.format("MM", date).equals(DateFormat.format("MM", date2)) && DateFormat.format("yyyy", date).equals(DateFormat.format("yyyy", date2))) {
            datetime = String.valueOf(DateFormat.format("HH:mm", date));
        }else{
            String year = String.valueOf(DateFormat.format("yyyy", date));
            datetime = DateFormat.format("dd/MM", date) + "/" + year.substring(year.length() - 2);
        }

        System.out.println("Orario chat: " + datetime);
        timeLastMessageTextView.setText(datetime);
        //lastMessageTimeTextView.setText(timeLastMessage);

    }

    @Override
    public int getItemCount() {
        return chatCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (chatCursor != null) {
            chatCursor.close();
        }

        chatCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnChatClickListener listener;

        private ConstraintLayout chatlayout;
        private ImageView profilePic;
        private TextView name, lastMessage, timeLastMessage;

        private ViewHolder(View itemView, OnChatClickListener listener) {
            super(itemView);
            this.listener = listener;

            chatlayout = itemView.findViewById(R.id.chatLayout);
            chatlayout.setOnClickListener(this);
            profilePic = itemView.findViewById(R.id.profilePhoto);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timeLastMessage = itemView.findViewById(R.id.timeLastMessage);

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.chatLayout:
                    listener.openChat(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }

        public interface OnChatClickListener {
            void openChat(int p);
        }

    }


}