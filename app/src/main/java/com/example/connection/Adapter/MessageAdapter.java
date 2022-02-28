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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Database.Database;
import com.example.connection.Controller.Task;
import com.example.connection.Model.Chat;
import com.example.connection.Model.Message;
import com.example.connection.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private ArrayList<Message> messagesList;
    public ArrayList<String> selectedMessage = new ArrayList<>();

    public MessageAdapter(Context context, Database database, String id, ArrayList<Message> messagesList, LinearLayoutManager linearLayoutManager) {
        this.context = context;
        this.database = database;
        this.idChat = id;
        this.messagesList = messagesList;
        this.linearLayoutManager = linearLayoutManager;
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
            return new ReceivedViewHolder(view);
        }
        //dateMessageLayout = view.findViewById(R.id.dateMessageLayout);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (messagesList.get(position) == null) {
            return;
        }

        Message message = messagesList.get(position);

        TextView textMessage = null;
        TextView messageTime = null;

        String datetime = message.getDate();
        if (datetime.split("£€")[0].equals("date")) if (checkDate()) return;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = new Date();
        try {
            date = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT)) {
            if(selectedMessage.contains(message.getIdMessage())){
                ((SentViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColorSemiTransparent));
            }else{
                ((SentViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            textMessage = ((SentViewHolder) holder).message;
            messageTime = ((SentViewHolder) holder).messageTime;
        } else if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED)) {
            if(selectedMessage.contains(message.getIdMessage())){
                ((ReceivedViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColorSemiTransparent));
            }else{
                ((ReceivedViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            textMessage = ((ReceivedViewHolder) holder).message;
            messageTime = ((ReceivedViewHolder) holder).messageTime;
        }

        textMessage.setText(message.getMessage());
        if (textMessage.getText().toString().length() > 400) setMessageWithClickableLink(textMessage);
        messageTime.setText(String.valueOf(date.getHours() < 10 ? '0' : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : "") + date.getMinutes());

        holder.itemView.setTag(message.getIdMessage());
        if (message.getSent().equals("0"))
            ((SentViewHolder) holder).icError.setVisibility(View.VISIBLE);
        //TODO AGGIUNGERE DIALOG SPIEGAZIONE ERRORE INVIO MESSSAGGIO
    }

    @Override
    public int getItemViewType(int position) {
        if (!messagesList.get(position).getIdSender().equals(database.getMyInformation()[0])) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }

    public void swapCursor(Cursor newMessageList) {
        newMessageList.moveToFirst();
        messagesList.clear();

        do {
            messagesList.add(new Message(newMessageList.getString(0), newMessageList.getString(1), newMessageList.getString(2), newMessageList.getString(4),messageCursor.getString(5)));
        } while (newMessageList.moveToNext());

        if (messagesList != null) {
            notifyDataSetChanged();
        }

    }

    public void removeMessage(String id){
        Iterator<Message> iterator = messagesList.iterator();

        while (iterator.hasNext()) {
            Message message = iterator.next();
            if(message.getIdMessage().equals(id)){
                iterator.remove();
                int position = messagesList.indexOf(message);
                messagesList.remove(message);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, messagesList.size());
            }
        }
    }

    public void addMessage(Message message){
        messagesList.add(message);
        notifyItemInserted(messagesList.size() - 1);
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout messageLayout, textLayout;
        private TextView message, messageTime;

        private ReceivedViewHolder(View itemView) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            textLayout = itemView.findViewById(R.id.textLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            message = itemView.findViewById(R.id.message);

        }

    }

    public static class SentViewHolder extends RecyclerView.ViewHolder{

        private LinearLayout messageLayout, textLayout;
        private TextView message, messageTime;
        private ImageView icError;

        private SentViewHolder(View itemView) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            textLayout = itemView.findViewById(R.id.textLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            message = itemView.findViewById(R.id.message);
            icError = itemView.findViewById(R.id.icErrorSendMessage);

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
