package com.example.connection.Listener;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.GlobalMessageAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.ChatController;
import com.example.connection.Database.Database;
import com.example.connection.Model.LastMessage;
import com.example.connection.Model.Message;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.View.ChatActivity;
import com.example.connection.View.ChatFragment;
import com.example.connection.View.Connection;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class MessageListener extends BroadcastReceiver {

    public MessageAdapter messageAdapter;
    private static MessageListener messageListener;
    private ChatAdapter chatAdapter;
    private GlobalMessageAdapter globalMessageAdapter;
    private Context context;
    private HashMap<String, NotificationCompat.MessagingStyle> messagingStyleHashMap;
    private HashMap<String, Integer> pendingIds;
    private int counter = 2;
    private Database database;
    private ChatController chatController;

    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    public static MessageListener newInstance(Context context, Database database, ChatController chatController) {
        messageListener = new MessageListener();
        messageListener.setContext(context);
        messageListener.setMessagingStyleHashMap();
        messageListener.setPendingIds();
        messageListener.setDatabase(database);
        messageListener.setChatController(chatController);
        return messageListener;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public static MessageListener getIstance() {
        return messageListener;
    }

    public void setMessageAdapter(MessageAdapter adapter) {
        this.messageAdapter = adapter;
    }

    public void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
    }

    public void setGlobalMessageAdapter(GlobalMessageAdapter globalMessageAdapter) {
        this.globalMessageAdapter = globalMessageAdapter;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public HashMap<String, NotificationCompat.MessagingStyle> getMessagingStyleHashMap() {
        return messagingStyleHashMap;
    }

    public HashMap<String, Integer> getPendingIds() {
        return pendingIds;
    }

    public void setPendingIds() {
        this.pendingIds = new HashMap<>();
    }

    public void setMessagingStyleHashMap() {
        this.messagingStyleHashMap = new HashMap<>();
    }

    public void setChatController(ChatController chatController) {
        this.chatController = chatController;
    }

    private void checkNotificationChannelEnabled() {

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent chatActivity = new Intent(messageListener.getContext(), ChatActivity.class);
        if (intent.getStringExtra("intentType").equals("messageController")) {
            switch (intent.getStringExtra("communicationType")) {
                case "tcp":
                    if (Connection.idChatOpen.equals(intent.getStringExtra("idChat"))) {
                        LastMessage lastMessage = database.getLastMessageChat(intent.getStringExtra("idChat"));
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        Date date = new Date();
                        try {
                            date = format.parse(lastMessage.getDateTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        messageListener.messageAdapter.addMessage(new Message(database.getLastMessageId(intent.getStringExtra("idChat")), intent.getStringExtra("idUser"), lastMessage.getLastMessage(), date.toString()));
                    } else if (Connection.fragmentName.equals("CHAT")) {
                        ChatFragment chatFragment = ChatFragment.getIstance();
                        chatFragment.setupRecyclerView(chatFragment.requireView());
                    } else {
                        User user = Connection.database.getUser(intent.getStringExtra("idChat"));
                        chatActivity.putExtra("idChat", user.getIdUser());
                        chatActivity.putExtra("username", user.getUsername());
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageListener.getContext());
                        NotificationCompat.MessagingStyle messagingStyle;
                        if (messageListener.getMessagingStyleHashMap().get(user.getIdUser()) != null) {
                            messagingStyle = messageListener.getMessagingStyleHashMap().get(user.getIdUser());
                            messagingStyle.addMessage(intent.getStringExtra("msg"), System.currentTimeMillis(), (Person) null);
                            messageListener.getMessagingStyleHashMap().replace(user.getIdUser(), messagingStyle);
                        } else {
                            messagingStyle = new NotificationCompat.MessagingStyle(user.getUsername());
                            messagingStyle.addMessage(intent.getStringExtra("msg"), System.currentTimeMillis(), (Person) null);
                            messageListener.getMessagingStyleHashMap().put(user.getIdUser(), messagingStyle);
                        }
                        PendingIntent chatIntent;
                        try {
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        } catch (NullPointerException e) {
                            messageListener.getPendingIds().put(intent.getStringExtra("idChat"), messageListener.counter);
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            messageListener.counter++;
                        }

                        String replyLabel = messageListener.getContext().getResources().getString(R.string.reply_label);
                        RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                .setLabel(replyLabel)
                                .build();

                        Intent replyIntent = new Intent(context, this.getClass());
                        replyIntent.putExtra("intentType", "messageController");
                        replyIntent.putExtra("communicationType", "reply");
                        replyIntent.putExtra("idChat", intent.getStringExtra("idChat"));
                        replyIntent.putExtra("username", user.getUsername());

                        // Build a PendingIntent for the reply action to trigger.
                        PendingIntent replyPendingIntent =
                                PendingIntent.getBroadcast(messageListener.getContext(),
                                        messageListener.getPendingIds().get(intent.getStringExtra("idChat")),
                                        replyIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                        // Create the reply action and add the remote input.
                        NotificationCompat.Action action =
                                new NotificationCompat.Action.Builder(R.drawable.ic_send_from_notification,
                                        messageListener.getContext().getString(R.string.reply_label), replyPendingIntent)
                                        .addRemoteInput(remoteInput)
                                        .setAllowGeneratedReplies(true)
                                        .build();

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageListener.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(messagingStyle)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                .setGroup("CHAT_GROUP")
                                .setContentIntent(chatIntent)
                                .addAction(action)
                                .setAutoCancel(true);

                        Notification summaryNotification = new NotificationCompat.Builder(messageListener.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setGroup("CHAT_GROUP")
                                .setGroupSummary(true)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(messageListener.getPendingIds().get(intent.getStringExtra("idChat")), notificationBuilder.build());
                        notificationManager.notify(0, summaryNotification);
                        if (Connection.fragmentName.equals("chat")) {
                            messageListener.chatAdapter.swapCursor(Connection.database.getAllChat());
                        }
                    }
                    break;
                case "multicast":
                    //check if i'm in global chat
                    if (Connection.isGlobalChatOpen) {

                    } else {
                        //refresh chat
                    }
                    break;
                case "reply":
                    messageListener.database.setReadMessage(intent.getStringExtra("idChat"), database.getLastMessageId(intent.getStringExtra("idChat")));
                    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                    if (remoteInput != null) {
                        //messageController.getTcpClient().sendMessage(String.valueOf(remoteInput.getCharSequence(KEY_TEXT_REPLY)),intent.getStringExtra("idChat"));
                        String reply = String.valueOf(remoteInput.getCharSequence(KEY_TEXT_REPLY));
                        messageListener.chatController.sendTCPMsg(reply, intent.getStringExtra("idChat"));
                        NotificationCompat.MessagingStyle messagingStyle = messageListener.getMessagingStyleHashMap().get(intent.getStringExtra("idChat"));
                        messagingStyle.addMessage(reply, System.currentTimeMillis(), (Person) null);
                        messageListener.getMessagingStyleHashMap().put(intent.getStringExtra("idChat"), messagingStyle);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageListener.getContext());

                        String replyLabel = messageListener.getContext().getResources().getString(R.string.reply_label);
                        RemoteInput newRemoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                .setLabel(replyLabel)
                                .build();

                        // Build a PendingIntent for the reply action to trigger.
                        PendingIntent replyPendingIntent =
                                PendingIntent.getBroadcast(messageListener.getContext(),
                                        messageListener.getPendingIds().get(intent.getStringExtra("idChat")),
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                        // Create the reply action and add the remote input.
                        NotificationCompat.Action action =
                                new NotificationCompat.Action.Builder(R.drawable.ic_send_from_notification,
                                        messageListener.getContext().getString(R.string.reply_label), replyPendingIntent)
                                        .addRemoteInput(newRemoteInput)
                                        .setAllowGeneratedReplies(true)
                                        .build();

                        PendingIntent chatIntent;
                        try {
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        } catch (NullPointerException e) {
                            messageListener.getPendingIds().put(intent.getStringExtra("idChat"), messageListener.counter);
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            messageListener.counter++;
                        }

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageListener.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(messagingStyle)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                .setGroup("CHAT_GROUP")
                                .setContentIntent(chatIntent)
                                .addAction(action)
                                .setAutoCancel(true);

                        Notification summaryNotification = new NotificationCompat.Builder(messageListener.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setGroup("CHAT_GROUP")
                                .setGroupSummary(true)
                                .setAutoCancel(true)
                                .build();

                        notificationManager.notify(messageListener.getPendingIds().get(intent.getStringExtra("idChat")), notificationBuilder.build());
                        notificationManager.notify(0, summaryNotification);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
