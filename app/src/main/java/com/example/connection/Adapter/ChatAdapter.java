package com.example.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
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

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    private Cursor cursor;

    public ChatAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.chat_layout, parent, false);
        ViewHolder holder = new ViewHolder(view, new ViewHolder.OnChatClickListener(){

            @Override
            public void openChat(int p) {
                cursor.moveToPosition(p);
                final long id = cursor.getLong(cursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        if(!cursor.moveToPosition(position)){
            return;
        }

        String nameUser = cursor.getString(cursor.getColumnIndex(Task.TaskEntry.NAME));
        long id = cursor.getLong(cursor.getColumnIndex(Task.TaskEntry.ID_USER));

        TextView name = holder.name;
        name.setText(nameUser);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount(){
        return cursor.getCount();
    }

    public void swapCursor(Cursor newCursor){
        if(cursor != null){
            cursor.close();
        }

        cursor = newCursor;

        if (newCursor != null){
            notifyDataSetChanged();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        OnChatClickListener listener;

        private ConstraintLayout chatlayout;
        private ImageView profilePhoto;
        private TextView name, lastMessage, timeLastMessage;

        private ViewHolder(View itemView, OnChatClickListener listener){
            super(itemView);
            this.listener = listener;

            chatlayout = itemView.findViewById(R.id.chatLayout);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
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
