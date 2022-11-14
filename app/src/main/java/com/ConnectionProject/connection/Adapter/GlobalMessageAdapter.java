package com.ConnectionProject.connection.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Model.GlobalMessage;
import com.ConnectionProject.connection.R;
import com.ConnectionProject.connection.View.BottomSheetNewChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class GlobalMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
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
    private static final int VIEW_TYPE_DATE_MESSAGE = 3;
    private ArrayList<GlobalMessage> messageList;
    private RecyclerView recyclerView;
    private ImageView noMessageImageView;
    private TextView noMessageTextView;
    public String idSelectedMessage = "";

    public GlobalMessageAdapter(Context context, Database database, String id, ArrayList<GlobalMessage> messageList, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView, ImageView noMessageImageView, TextView noMessageTextView) {
        this.context = context;
        this.database = database;
        this.idChat = id;
        this.messageList = messageList;
        this.linearLayoutManager = linearLayoutManager;
        this.recyclerView = recyclerView;
        this.noMessageImageView = noMessageImageView;
        this.noMessageTextView = noMessageTextView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.lyt_message_sent_global, parent, false);
            return new SentViewHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.lyt_message_received_global, parent, false);
            return new ReceivedViewHolder(view, new ReceivedViewHolder.OnChatClickListener() {
                @Override
                public void openChat(int p) {
                    BottomSheetNewChat bottomSheet = new BottomSheetNewChat(database.getAllUserInformation(messageList.get(p).getIdSender()), false);
                    bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(), "ModalBottomSheet");
                }
            });
        } else if (viewType == VIEW_TYPE_DATE_MESSAGE) {
            view = inflater.inflate(R.layout.lyt_date_message, parent, false);
            return new DateMessageViewHolder(view);
        }
        //dateMessageLayout = view.findViewById(R.id.dateMessageLayout);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (messageList.get(position) == null) {
            return;
        }

        GlobalMessage message = messageList.get(position);

        TextView messageTextView = null, messageTimeTextView = null;

        String dateTime = message.getDate();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Date date = new Date();
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT)) {
            if (idSelectedMessage.equals(message.getIdMessage())) {
                ((GlobalMessageAdapter.SentViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColorSemiTransparent));
            } else {
                ((GlobalMessageAdapter.SentViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            messageTextView = ((SentViewHolder) holder).message;
            messageTimeTextView = ((SentViewHolder) holder).messageTime;
        } else if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED)) {
            ((ReceivedViewHolder) holder).username.setText(message.getUsername());
            if (idSelectedMessage.equals(message.getIdMessage())) {
                ((GlobalMessageAdapter.ReceivedViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColorSemiTransparent));
            } else {
                ((GlobalMessageAdapter.ReceivedViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            messageTextView = ((ReceivedViewHolder) holder).message;
            messageTimeTextView = ((ReceivedViewHolder) holder).messageTime;
        } else if (holder.getItemViewType() == VIEW_TYPE_DATE_MESSAGE) {
            ((DateMessageViewHolder) holder).message.setText((date.getDate() < 10 ? "0" + date.getDate() : "" + date.getDate()) + "/" +
                    (date.getMonth() + 1 < 10 ? "0" + (date.getMonth() + 1) : "" + (date.getMonth() + 1)) + "/" +
                    (date.getYear() + 1900));
            return;
        }

        messageTextView.setText(message.getMessage());
        if (messageTextView.getText().toString().length() > 400)
            setMessageWithClickableLink(messageTextView);
        messageTimeTextView.setText(String.valueOf(date.getHours() < 10 ? '0' : "") + date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : "") + date.getMinutes());

        holder.itemView.setTag(message.getIdMessage());

        if (messageList.size() > 2) {
            if (position == messageList.size() - 1 && linearLayoutManager.findLastVisibleItemPosition() == messageList.size() - 2) {
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).getMessage().isEmpty()) {
            return VIEW_TYPE_DATE_MESSAGE;
        } else if (database.getMyInformation() != null && !messageList.get(position).getIdSender().equals(database.getMyInformation()[0])) {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        } else {
            return VIEW_TYPE_MESSAGE_SENT;
        }
    }

    @Override
    public int getItemCount() {
        if (messageList == null) return 0;
        return messageList.size();
    }

    public void swapCursor(Cursor newMessageList) {
        newMessageList.moveToFirst();
        messageList.clear();

        do {
            messageList.add(new GlobalMessage(newMessageList.getString(0), newMessageList.getString(1), newMessageList.getString(2), newMessageList.getString(3), newMessageList.getString(4)));
        } while (newMessageList.moveToNext());

        if (messageList != null) {
            notifyDataSetChanged();
        }

    }

    public void addMessage(GlobalMessage message) {
        if (recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageImageView.setVisibility(View.INVISIBLE);
            noMessageTextView.setVisibility(View.INVISIBLE);
        }
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
        recyclerView.scrollToPosition(messageList.size()  - 1);
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        OnChatClickListener listener;

        private LinearLayout messageLayout;
        private ConstraintLayout textLayout;
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

    public static class DateMessageViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout messageLayout;
        private TextView message;

        private DateMessageViewHolder(View itemView) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            message = itemView.findViewById(R.id.message);
        }

    }

    private boolean checkDate() {
        int firstPositionVisible = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        int lastPositionVisible = linearLayoutManager.findLastCompletelyVisibleItemPosition();
        int count = 0;
        for (int i = firstPositionVisible; i < lastPositionVisible; i++) {
            String datetime = messageList.get(i).getDate();
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
