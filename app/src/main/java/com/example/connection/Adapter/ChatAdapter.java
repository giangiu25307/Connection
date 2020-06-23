package com.example.connection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.ChatActivity;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private Cursor chatCursor, userCursor;
    private Database database;
    private Bitmap bitmap;
    SimpleDateFormat format = new SimpleDateFormat();
    Date date=new Date();

    public ChatAdapter(Context context, Cursor chatCursor, Database database) {
        this.context = context;
        this.chatCursor = chatCursor;
        this.database = database;
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_layout, parent, false);
        ViewHolder holder = new ViewHolder(view, new ViewHolder.OnChatClickListener(){

            @Override
            public void openChat(int p) {
                chatCursor.moveToPosition(p);
                final String id = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
                Intent myIntent = new Intent(context, ChatActivity.class);
                myIntent.putExtra("idChat", id);
                //myIntent.putExtra("database", (Serializable) database);
                //myIntent.putExtra("key", database); //Optional parameters
                context.startActivity(myIntent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        if(!chatCursor.moveToPosition(position)){
            return;
        }

        String nameUser = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
        userCursor = database.getUSer(nameUser);
        userCursor.moveToFirst();
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(userCursor));
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));

        long id = chatCursor.getLong(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
        System.out.println(id);
        String userName = userCursor.getString(userCursor.getColumnIndex(Task.TaskEntry.USERNAME));
        String lastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.LAST_MESSAGE));
        String profilePicPosition = userCursor.getString(userCursor.getColumnIndex(Task.TaskEntry.PROFILE_PIC));
        String datetime = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.DATETIME));
        File profilePic = new  File(profilePicPosition);

        if(profilePic.exists()){

            bitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
            ImageView profilePicImageView = holder.profilePic;
            profilePicImageView.setImageBitmap(bitmap);
        }

        userCursor = database.getLastMessageChat(Task.TaskEntry.ID_CHAT);
        //String timeLastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.DATE));


        holder.itemView.setTag(id);

        TextView userNameTextView = holder.name;
        TextView lastMessageTextView = holder.lastMessage;
        TextView lastMessageTimeTextView = holder.name;
        TextView timeLastMessageTextView = holder.timeLastMessage;
        userNameTextView.setText(userName);
        lastMessageTextView.setText(lastMessage);
        try {
            date=format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datetime= date.getHours()+":"+date.getMinutes();
        timeLastMessageTextView.setText(datetime);
        //lastMessageTimeTextView.setText(timeLastMessage);

    }

    @Override
    public int getItemCount(){
        return chatCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(chatCursor != null){
            chatCursor.close();
        }

        chatCursor = newCursor;

        if (newCursor != null){
            notifyDataSetChanged();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnChatClickListener listener;

        private ConstraintLayout chatlayout;
        private ImageView profilePic;
        private TextView name, lastMessage, timeLastMessage;

        private ViewHolder(View itemView, OnChatClickListener listener){
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
