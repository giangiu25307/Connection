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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    private Context context;
    private Cursor chatCursor;
    private User user;
    private Database database;
    private Bitmap bitmap, bitmap2;
    private Button button;
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private Date date = new Date();
    private ChatController chatController;


    public RequestAdapter(Context context, Cursor chatCursor, Database database, ChatController chatController, Button button) {
        this.context = context;
        this.chatCursor = chatCursor;
        this.database = database;
        this.chatController = chatController;
        this.button = button;
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
                myIntent.putExtra("name", chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.NAME)));
                context.startActivity(myIntent);

            }

            @Override
            public void deleteRequest(int p) {
                chatCursor.moveToPosition(p);
                database.discard(chatCursor.getString(chatCursor.getColumnIndex(Task.TaskEntry.ID_CHAT)));
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

        TextView informationTextView = holder.information;
        TextView informationTextView2 = holder.information2;
        TextView lastMessageTextView = holder.lastMessage;
        TextView timeLastMessageTextView = holder.timeLastMessage;
        informationTextView.setText(user.getName());
        String temp = user.getAge() + ", " + user.getGender();
        informationTextView2.setText(temp);
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

        if (date.getDay() == date2.getDay() && date.getMonth() == date2.getMonth() && date.getYear() == date2.getYear()) {
            datetime = String.valueOf(date.getHours() < 10 ? '0' : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : "") + date.getMinutes();
        } else {
            datetime = String.valueOf(date.getDay() < 10 ? '0' : "") + date.getDay() + "/" + (date.getMonth() < 10 ? '0' : "") + date.getMonth() + "/" + String.valueOf(date.getYear()).substring(String.valueOf(date.getYear()).length() - 2, String.valueOf(date.getYear()).length());
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
            int totalRequest = chatCursor.getCount();
            button.setText(totalRequest == 1 ? totalRequest + " request" : totalRequest + " requests");
            notifyDataSetChanged();
        }else{
            button.setText(0 + " requests");
        }

    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        RequestAdapter.ViewHolder.OnChatClickListener listener;

        private ImageView profilePic, answer, cancel;
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
