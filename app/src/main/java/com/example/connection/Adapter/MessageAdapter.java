package com.example.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private Context context;
    private Cursor messageCursor, userCursor;
    private Database database;
    private String idChat;
    private Bitmap bitmap;
    SimpleDateFormat format = new SimpleDateFormat();
    Date date = new Date();
    int counter;
    LinearLayoutManager linearLayoutManager;
    int previousCount = 0;
    TextView dateMessageLayout;

    public MessageAdapter(Context context, Database database, String id, Cursor messageCursor, LinearLayoutManager linearLayoutManager) {
        this.context = context;
        this.database = database;
        this.idChat = id;
        this.messageCursor = messageCursor;
        this.linearLayoutManager = linearLayoutManager;
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(messageCursor));
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.message_layout, parent, false);
        dateMessageLayout = view.findViewById(R.id.dateMessageLayout);
        ViewHolder holder = new ViewHolder(view, new ViewHolder.OnChatClickListener() {


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
        if (!messageCursor.moveToPosition(position)) {
            return;
        }
        String datetime = messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.DATETIME));
        if (datetime.split("£€")[0].equals("date")) if (checkDate()) return;
        LinearLayout messageLayout = holder.messageLayout;
        LinearLayout textLayout = holder.textLayout;
        TextView message = holder.message;
        message.setText(messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.MSG)));

        TextView messageTime = holder.messageTime;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = new Date();
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        messageTime.setText(String.valueOf(date.getHours() < 10 ? '0' : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : "") + date.getMinutes());


        if (!messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.ID_SENDER)).equals(database.getMyInformation()[0])) {
            textLayout.setBackgroundResource(R.drawable.message_rounded_black_background);
            message.setTextColor(Color.WHITE);
            messageLayout.setGravity(Gravity.LEFT);
        } else {
            textLayout.setBackgroundResource(R.drawable.message_rounded_white_background);
            message.setTextColor(Color.BLACK);
            messageLayout.setGravity(Gravity.RIGHT);
        }

        holder.itemView.setTag(idChat);


    }

    @Override
    public int getItemCount() {
        return messageCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (messageCursor != null) {
            messageCursor.close();
        }

        messageCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnChatClickListener listener;

        private LinearLayout messageLayout, textLayout;
        private TextView message, messageTime;

        private ViewHolder(View itemView, OnChatClickListener listener) {
            super(itemView);
            this.listener = listener;

            messageLayout = itemView.findViewById(R.id.messageLayout);
            textLayout = itemView.findViewById(R.id.textLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageLayout.setOnClickListener(this);
            message = itemView.findViewById(R.id.message);

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

    private boolean checkDate() {
        int firstPositionVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastPositionVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int count = 0;
        for (int i = firstPositionVisible; i < lastPositionVisible; i++) {
            messageCursor.moveToPosition(i);
            String datetime = messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.DATETIME));
            if (datetime.split("£€")[0].equals("date")) {
                count++;
                try {
                    date = format.parse(datetime.split("£€")[1]);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (date.compareTo(format.parse(String.valueOf(LocalDateTime.now()))) != 0) {
                if (count == 0 && previousCount == 1 && dateMessageLayout.getVisibility() == TextView.INVISIBLE) {
                    dateMessageLayout.setVisibility(TextView.VISIBLE);
                    dateMessageLayout.setText(String.valueOf(date.getDay() < 10 ? '0' : "") + (date.getMonth() < 10 ? '0' : "") + (date.getYear() < 10 ? '0' : ""));
                    return true;
                } else if (count == 1 && dateMessageLayout.getVisibility() == TextView.VISIBLE) {
                    dateMessageLayout.setVisibility(TextView.INVISIBLE);
                    return true;
                }
            } else {
                dateMessageLayout.setVisibility(TextView.INVISIBLE);
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        previousCount = count;
        return false;
    }

}
