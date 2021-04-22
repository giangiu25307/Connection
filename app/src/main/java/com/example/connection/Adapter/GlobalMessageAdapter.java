package com.example.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Controller.Task;
import com.example.connection.Database.Database;
import com.example.connection.R;
import com.example.connection.View.BottomSheetNewChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class GlobalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private Cursor messageCursor, userCursor;
    private Database database;
    private String idChat;
    private Bitmap bitmap;
    private int receivedMessageTextColor;
    SimpleDateFormat format = new SimpleDateFormat();
    Date date = new Date();
    int counter;
    LinearLayoutManager linearLayoutManager;
    int previousCount = 0;
    TextView dateMessageLayout;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    public GlobalMessageAdapter(Context context, Database database, String id, Cursor messageCursor, LinearLayoutManager linearLayoutManager, int receivedMessageTextColor) {
        this.context = context;
        this.database = database;
        this.idChat = id;
        this.messageCursor = messageCursor;
        this.linearLayoutManager = linearLayoutManager;
        this.receivedMessageTextColor = receivedMessageTextColor;
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(messageCursor));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.lyt_message_sent, parent, false);
            return new SentViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.lyt_message_received, parent, false);
            return new ReceivedViewHolder(view, new ReceivedViewHolder.OnChatClickListener() {
                @Override
                public void openChat(int p) {
                    messageCursor.moveToPosition(p);
                    BottomSheetNewChat bottomSheet = new BottomSheetNewChat(database.getAllUserInformation(messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.ID_USER))));
                    bottomSheet.show(((AppCompatActivity)context).getSupportFragmentManager(), "ModalBottomSheet");
                }
            });
        }
        //dateMessageLayout = view.findViewById(R.id.dateMessageLayout);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!messageCursor.moveToPosition(position)) {
            return;
        }

        TextView message = null;
        TextView messageTime = null;

        String datetime = messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.DATETIME));
        if (datetime.split("£€")[0].equals("date")) if (checkDate()) return;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = new Date();
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT)) {
            message = ((SentViewHolder) holder).message;
            messageTime = ((SentViewHolder) holder).messageTime;
        } else if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED)) {
            ((ReceivedViewHolder) holder).username.setText(messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.USERNAME)));
            message = ((ReceivedViewHolder) holder).message;
            messageTime = ((ReceivedViewHolder) holder).messageTime;
        }

        message.setText(messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.MSG)));
        if (message.getText().toString().length() > 400) setMessageWithClickableLink(message);
        messageTime.setText(String.valueOf(date.getHours() < 10 ? '0' : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : "") + date.getMinutes());

        holder.itemView.setTag(idChat);

    }

    @Override
    public int getItemViewType(int position) {
        messageCursor.moveToPosition(position);
        if (!messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.ID_SENDER)).equals(database.getMyInformation()[0])) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return VIEW_TYPE_MESSAGE_SENT;
        }
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

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnChatClickListener listener;

        private LinearLayout messageLayout, textLayout;
        private TextView username, message, messageTime;

        private ReceivedViewHolder(View itemView, OnChatClickListener listener) {
            super(itemView);
            this.listener = listener;

            messageLayout = itemView.findViewById(R.id.messageLayout);
            textLayout = itemView.findViewById(R.id.textLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            messageLayout.setOnClickListener(this);
            username = itemView.findViewById(R.id.usernameTextView);
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

    public static class SentViewHolder extends RecyclerView.ViewHolder {


        private LinearLayout messageLayout, textLayout;
        private TextView message, messageTime;

        private SentViewHolder(View itemView) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            textLayout = itemView.findViewById(R.id.textLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            message = itemView.findViewById(R.id.message);

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

    public void setMessageWithClickableLink(final TextView textViewOriginal) {
        final String fullText = textViewOriginal.getText().toString();
        String spannable = fullText.substring(0, 400) + "... continue reading";
        SpannableString ss = new SpannableString(spannable);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                textViewOriginal.setText(fullText);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };

        ss.setSpan(clickableSpan, 404, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.blue)), 404, spannable.length(), 0);


        textViewOriginal.setText(ss);
        textViewOriginal.setMovementMethod(LinkMovementMethod.getInstance());
        textViewOriginal.setHighlightColor(Color.TRANSPARENT);
    }

}
