package com.example.connection.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Controller.ChatController;
import com.example.connection.Database.Database;
import com.example.connection.Controller.Task;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.View.ChatActivity;
import com.example.connection.View.ChatFragment;
import com.example.connection.View.Connection;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private Cursor chatCursor;
    private User user;
    private Database database;
    private Bitmap bitmap, bitmap2;
    private Button requestButton;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private Date date = new Date();
    private ChatController chatController;
    private TextView dialogTitle;


    public RequestAdapter(Context context, Cursor chatCursor, Database database, ChatController chatController, Button requestButton, TextView dialogTitle) {
        this.context = context;
        this.chatCursor = chatCursor;
        this.database = database;
        this.chatController = chatController;
        this.requestButton = requestButton;
        this.dialogTitle = dialogTitle;
        Log.v("Request Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.lyt_request, parent, false);
        ViewHolder holder = new ViewHolder(view, new RequestAdapter.ViewHolder.OnChatClickListener() {

            @Override
            public void openChat(int p) {
                chatCursor.moveToPosition(p);
                final String id = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
                Intent myIntent = new Intent(context, ChatActivity.class);
                // myIntent.putExtra("chatController", chatController); //Optional parameters\
                myIntent.putExtra("idChat", id);
                myIntent.putExtra("username", chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.USERNAME)));
                context.startActivity(myIntent);

            }

            @Override
            public void deleteRequest(int p) {
                chatCursor.moveToPosition(p);
                database.blockUser(chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT)));
                swapCursor(database.getAllRequestChat());
            }

        }, context, chatCursor);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdapter.ViewHolder holder, int position) {
        if (!chatCursor.moveToPosition(position)) {
            return;
        }
        String nameUser = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
        user = database.getUser(nameUser);
        //Log.v("Request Cursor Object", DatabaseUtils.dumpCursorToString(userCursor));
        //Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(chatCursor));

        long id = chatCursor.getLong(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT));
        String lastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.LAST_MESSAGE));
        String datetime = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.DATETIME));
        File profilePic = new File(user.getProfilePic());

        if(profilePic.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(profilePic.getAbsolutePath());
            holder.profilePic.setImageBitmap(myBitmap);
        }

        //String timeLastMessage = chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.DATE));

        holder.itemView.setTag(id);

        TextView informationTextView = holder.information;
        TextView informationTextView2 = holder.information2;
        TextView lastMessageTextView = holder.lastMessage;
        TextView timeLastMessageTextView = holder.timeLastMessage;
        informationTextView.setText(user.getUsername());
        String temp = user.getAge() + ", " + user.getGender();
        informationTextView2.setText(temp);
        if (lastMessage != null) {
            lastMessageTextView.setText(lastMessage);
            lastMessageTextView.setTypeface(null, Typeface.NORMAL);
        }else{
            lastMessageTextView.setText("No message");
            lastMessageTextView.setTypeface(null, Typeface.ITALIC);
        }
        if(datetime != null){
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

            if (date.getDay() == date2.getDay() && date.getMonth() == date2.getMonth() && date.getYear() == date2.getYear()) {
                datetime = String.valueOf(date.getHours() < 10 ? '0' : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : "") + date.getMinutes();
            } else {
                datetime = String.valueOf(date.getDay() < 10 ? '0' : "") + date.getDay() + "/" + (date.getMonth() < 10 ? '0' : "") + date.getMonth() + "/" + String.valueOf(date.getYear()).substring(String.valueOf(date.getYear()).length() - 2, String.valueOf(date.getYear()).length());
            }

            System.out.println("Orario chat: " + datetime);
            timeLastMessageTextView.setText(datetime);
        }else{
            timeLastMessageTextView.setText("");
        }

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
            int totalRequest = chatCursor.getCount();
            ChatFragment.getIstance().setTotalRequest(totalRequest);
            if(totalRequest == 0){
                ChatFragment.setupRequestRecyclerView();
            }else{
                requestButton.setText(totalRequest <= 1 ? totalRequest + " request" : totalRequest + " requests");
                dialogTitle.setText(totalRequest <= 1 ? " Request (" + totalRequest + ")" : " Requests (" + totalRequest + ")");
                notifyDataSetChanged();
            }
        }else{
            requestButton.setText(0 + " request");
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RequestAdapter.ViewHolder.OnChatClickListener listener;

        private ImageView profilePic;
        private ImageButton answer, cancel;
        private TextView information, information2, lastMessage, timeLastMessage;
        private Cursor chatCursor;

        private ViewHolder(View itemView, RequestAdapter.ViewHolder.OnChatClickListener listener, Context context, Cursor chatCursor) {
            super(itemView);
            this.listener = listener;
            this.chatCursor = chatCursor;
            profilePic = itemView.findViewById(R.id.profilePic);
            information = itemView.findViewById(R.id.textViewInformation);
            information2 = itemView.findViewById(R.id.textViewInformation2);
            lastMessage = itemView.findViewById(R.id.textViewMessage);
            timeLastMessage = itemView.findViewById(R.id.textViewDate);
            answer = itemView.findViewById(R.id.reply);
            answer.setOnClickListener(this);
            cancel = itemView.findViewById(R.id.cancel);
            cancel.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.reply:
                    listener.openChat(this.getLayoutPosition());
                    break;
                case R.id.cancel:
                    listener.deleteRequest(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }

        public interface OnChatClickListener {
            void openChat(int p);
            void deleteRequest(int p);
        }

    }

}
