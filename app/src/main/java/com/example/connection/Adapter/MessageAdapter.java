package com.example.connection.Adapter;

import android.content.Context;
import android.content.res.Resources;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connection.Controller.ChatController;
import com.example.connection.Controller.Task;
import com.example.connection.Database.Database;
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
    SimpleDateFormat format;
    Date date = new Date();
    int counter;
    LinearLayoutManager linearLayoutManager;
    int previousCount = 0;
    TextView dateMessageLayout;
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_DATE_MESSAGE = 3;
    private ArrayList<Message> messagesList;
    public ArrayList<String> selectedMessage = new ArrayList<>();
    private ImageView noMessageImageView;
    private TextView noMessageTextView;
    private RecyclerView recyclerView;
    private @ColorInt
    int dateLayoutTextColor;

    public MessageAdapter(Context context, Database database, String id, ArrayList<Message> messagesList, LinearLayoutManager linearLayoutManager, RecyclerView recyclerView, ImageView noMessageImageView, TextView noMessageTextView) {
        this.context = context;
        this.database = database;
        this.idChat = id;
        this.messagesList = messagesList;
        this.linearLayoutManager = linearLayoutManager;
        this.recyclerView = recyclerView;
        this.noMessageImageView = noMessageImageView;
        this.noMessageTextView = noMessageTextView;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(messageCursor));
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(R.attr.textColor, typedValue, true);
        dateLayoutTextColor = typedValue.data;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;
        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = inflater.inflate(R.layout.lyt_message_sent, parent, false);
            SentViewHolder sentViewHolder = new SentViewHolder(view, new SentViewHolder.OnAlertIconListener() {
                @Override
                public void openDialogMessageNotSent(int p) {
                    if (database.findIp(idChat) == null) {
                        Toast.makeText(context, "User not connected", Toast.LENGTH_SHORT).show();
                    } else {
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context, R.style.CustomAlertDialog);
                        dialogBuilder.setView(R.layout.dialog_retry_message);
                        final AlertDialog alertDialog = dialogBuilder.create();
                        alertDialog.show();
                        alertDialog.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.findViewById(R.id.retryButton).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                alertDialog.dismiss();
                                ChatController chatController = ChatController.getInstance();
                                Message message = messagesList.get(p);
                                database.deleteMessage(message.getIdMessage(), idChat);
                                notifyItemRemoved(p);
                                chatController.reSendTCPMsg(message.getMessage(), idChat);
                            }
                        });
                    }
                }
            });
            return sentViewHolder;
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = inflater.inflate(R.layout.lyt_message_received, parent, false);
            return new ReceivedViewHolder(view);
        } else if (viewType == VIEW_TYPE_DATE_MESSAGE) {
            view = inflater.inflate(R.layout.lyt_date_message_layout, parent, false);
            return new DateMessageViewHolder(view);
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

        TextView messageTextView = null, messageTimeTextView = null;

        String dateTime = message.getDate();

        Date date = new Date();
        try {
            date = format.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_SENT)) {
            if (selectedMessage.contains(message.getIdMessage())) {
                ((SentViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColorSemiTransparent));
            } else {
                ((SentViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            messageTextView = ((SentViewHolder) holder).message;
            messageTimeTextView = ((SentViewHolder) holder).messageTime;

            ((SentViewHolder) holder).progressBar.setVisibility(View.INVISIBLE);

            if (message.getSent().equals("0")) {
                ((SentViewHolder) holder).icError.setVisibility(View.VISIBLE);
            } else {
                ((SentViewHolder) holder).icError.setVisibility(View.INVISIBLE);
            }
        } else if ((holder.getItemViewType() == VIEW_TYPE_MESSAGE_RECEIVED)) {
            if (selectedMessage.contains(message.getIdMessage())) {
                ((ReceivedViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.secondaryColorSemiTransparent));
            } else {
                ((ReceivedViewHolder) holder).messageLayout.setBackgroundColor(context.getResources().getColor(R.color.transparent));
            }
            messageTextView = ((ReceivedViewHolder) holder).message;
            messageTimeTextView = ((ReceivedViewHolder) holder).messageTime;
        } else if ((holder.getItemViewType() == VIEW_TYPE_DATE_MESSAGE)) {
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

        if (messagesList.size() > 2) {
            if (position == messagesList.size() - 1 && linearLayoutManager.findLastVisibleItemPosition() == messagesList.size() - 2) {
                recyclerView.scrollToPosition(messagesList.size() - 1);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (messagesList.get(position).getMessage().isEmpty()) {
            return VIEW_TYPE_DATE_MESSAGE;
        } else if (database.getMyInformation() != null && !messagesList.get(position).getIdSender().equals(database.getMyInformation()[0])) {
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
            messagesList.add(new Message(newMessageList.getString(0), newMessageList.getString(1), newMessageList.getString(2), newMessageList.getString(4), messageCursor.getString(5)));
        } while (newMessageList.moveToNext());

        if (messagesList != null) {
            notifyDataSetChanged();
        }

    }

    public void removeMessage(String id) {
        Iterator<Message> iterator = messagesList.iterator();

        while (iterator.hasNext()) {
            Message message = iterator.next();
            if (message.getIdMessage().equals(id)) {
                iterator.remove();
                int position = messagesList.indexOf(message);
                messagesList.remove(message);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, messagesList.size());
            }
        }
    }

    public void addMessage(Message message) {
        if (recyclerView.getVisibility() == View.INVISIBLE) {
            recyclerView.setVisibility(View.VISIBLE);
            noMessageImageView.setVisibility(View.INVISIBLE);
            noMessageTextView.setVisibility(View.INVISIBLE);
        }
        messagesList.add(message);
        notifyItemInserted(messagesList.size() - 1);
        recyclerView.scrollToPosition(messagesList.size()  - 1);
    }

    public static class ReceivedViewHolder extends RecyclerView.ViewHolder {

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

    public static class SentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        MessageAdapter.SentViewHolder.OnAlertIconListener listener;

        private LinearLayout messageLayout, textLayout;
        private TextView message, messageTime;
        private ImageView icError;
        private ProgressBar progressBar;

        private SentViewHolder(View itemView, MessageAdapter.SentViewHolder.OnAlertIconListener listener) {
            super(itemView);

            messageLayout = itemView.findViewById(R.id.messageLayout);
            textLayout = itemView.findViewById(R.id.textLayout);
            messageTime = itemView.findViewById(R.id.messageTime);
            message = itemView.findViewById(R.id.message);
            icError = itemView.findViewById(R.id.icErrorSendMessage);
            progressBar = itemView.findViewById(R.id.progressBar);
            this.listener = listener;

        }

        @Override
        public void onClick(View view) {

            switch (view.getId()) {
                case R.id.icErrorSendMessage:
                    listener.openDialogMessageNotSent(this.getLayoutPosition());
                    break;
                default:
                    break;
            }
        }

        public interface OnAlertIconListener {
            void openDialogMessageNotSent(int p);
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
            messageCursor.moveToPosition(i);
            String message = messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.MESSAGE));
            String datetime = messageCursor.getString(messageCursor.getColumnIndex(Task.TaskEntry.DATETIME));
            if (message.equals("")) {
                count++;
                try {
                    date = format.parse(datetime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            if (date.compareTo(format.parse(String.valueOf(LocalDateTime.now()))) != 0) {
                if (count == 0 && previousCount == 1 && dateMessageLayout.getVisibility() == TextView.INVISIBLE) {
                    dateMessageLayout.setVisibility(TextView.VISIBLE);
                    dateMessageLayout.setText(String.valueOf(date.getDay() < 10 ? '0' : "") + "/" + (date.getMonth() < 10 ? '0' : "") + "/" + (date.getYear() < 10 ? '0' : ""));
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
