package com.example.connection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private Cursor messageCursor, userCursor;
    private Database database;
    private String idChat;
    private Bitmap bitmap;
    SimpleDateFormat format = new SimpleDateFormat();
    Date date=new Date();

    public MessageAdapter(Context context, Database database, String id) {
        this.context = context;
        this.database = database;
        this.idChat = id;
        this.messageCursor = database.getAllMsg(id);
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(messageCursor));
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_layout, parent, false);
        ViewHolder holder = new ViewHolder(view, new ViewHolder.OnChatClickListener(){



            @Override
            public void openChat(int p) {
                messageCursor.moveToPosition(p);
                //final long id = messageCursor.getLong(messageCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
            }
        });

        return holder;
        
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        if(!messageCursor.moveToPosition(position)){
            return;
        }

        TextView message = holder.message;
        message.setText(messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.MSG)));
        LinearLayout messageLayout = holder.messageLayout;

        System.out.println("Id sender: " + messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.ID_SENDER)) + " idChat: " + database.getMyInformation()[0]);

        if(!messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.ID_SENDER)).equals(database.getMyInformation()[0])){
            message.setBackgroundColor(Color.BLACK);
            message.setTextColor(Color.WHITE);
        }else{
            messageLayout.setGravity(Gravity.RIGHT);
        }

        holder.itemView.setTag(idChat);



        /*
        try {
            date=format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datetime= date.getHours()+":"+date.getMinutes();
        lastMessageTimeTextView.setText(timeLastMessage);
        */

    }

    @Override
    public int getItemCount(){
        return messageCursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(messageCursor != null){
            messageCursor.close();
        }

        messageCursor = newCursor;

        if (newCursor != null){
            notifyDataSetChanged();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnChatClickListener listener;

        private LinearLayout messageLayout;
        private TextView message;

        private ViewHolder(View itemView, OnChatClickListener listener){
            super(itemView);
            this.listener = listener;

            messageLayout = itemView.findViewById(R.id.messageLayout);
            messageLayout.setOnClickListener(this);
            message = itemView.findViewById(R.id.message);

            System.out.println("khsaivcviwv");
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.messageLayout:
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
