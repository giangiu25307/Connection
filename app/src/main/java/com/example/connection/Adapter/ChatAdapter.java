package com.example.connection.Adapter;

import android.content.Context;
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

import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.R;

import java.io.File;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private Cursor chatCursor, userCursor;
    private Database database;
    private Bitmap bitmap;

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
                final long id = chatCursor.getLong(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
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

        long id = chatCursor.getLong(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
        System.out.println(id);
        String userName = userCursor.getString(userCursor.getColumnIndex(Task.TaskEntry.USERNAME));
        String lastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.LAST_MESSAGE));
        String profilePicPosition = userCursor.getString(userCursor.getColumnIndex(Task.TaskEntry.PROFILE_PIC));
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
        userNameTextView.setText(userName);
        lastMessageTextView.setText(lastMessage);
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
            profilePic = itemView.findViewById(R.id.profilePhoto);
            name = itemView.findViewById(R.id.name);
            lastMessage = itemView.findViewById(R.id.lastMessage);
            timeLastMessage = itemView.findViewById(R.id.timeLastMessage);

        }

        @Override
        public void onClick(View view) {
            listener.openChat(this.getLayoutPosition());
        }

        public interface OnChatClickListener {
            void openChat(int p);
        }

    }

}
