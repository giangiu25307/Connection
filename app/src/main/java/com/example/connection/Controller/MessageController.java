package com.example.connection.Controller;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.R;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.View.ChatActivity;
import com.example.connection.View.Connection;

import java.util.HashMap;

public class MessageController extends BroadcastReceiver {

    public MessageAdapter messageAdapter;
    private static MessageController messageController;
    private ChatAdapter chatAdapter;
    private Context context;
    private HashMap<String, NotificationCompat.MessagingStyle> messagingStyleHashMap;
    private HashMap<String, Integer> pendingIds;
    private int counter=2;
    private TcpClient tcpClient;

    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    String replyLabel = messageController.getContext().getResources().getString(R.string.reply_label);
    RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
            .setLabel(replyLabel)
            .build();

    public static MessageController newInstance(Context context) {
        messageController = new MessageController();
        messageController.setContext(context);
        messageController.setMessagingStyleHashMap();
        messageController.setPendingIds();
        return messageController;
    }

    public static MessageController getIstance(){
        return messageController;
    }

    public void setMessageAdapter(MessageAdapter adapter){
        this.messageAdapter = adapter;
    }

    public void setChatAdapter(ChatAdapter chatAdapter) {
        this.chatAdapter = chatAdapter;
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

    public TcpClient getTcpClient() {
        return tcpClient;
    }

    public void setTcpClient(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent chatActivity = new Intent(messageController.getContext(),ChatActivity.class);
        if(intent.getStringExtra("intentType").equals("messageController")) {
            switch (intent.getStringExtra("communicationType")) {
                case "tcp":
                    if (Connection.idChatOpen.equals(intent.getStringExtra("idChat"))) {
                        messageController.messageAdapter.swapCursor(Connection.database.getAllMsg(intent.getStringExtra("idChat")));
                    } else {
                        Cursor user = Connection.database.getUser(intent.getStringExtra("idChat"));
                        user.moveToFirst();
                        chatActivity.putExtra("idChat",user.getString(0));
                        chatActivity.putExtra("username",user.getString(1));
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageController.getContext());
                        NotificationCompat.MessagingStyle messagingStyle;
                        if(messageController.getMessagingStyleHashMap().get(user.getString(0)) != null){
                            messagingStyle = messageController.getMessagingStyleHashMap().get(user.getString(0));
                            messagingStyle.addMessage(intent.getStringExtra("msg"), System.currentTimeMillis(), (Person) null);
                            messageController.getMessagingStyleHashMap().replace(user.getString(0), messagingStyle);
                        }else{
                            messagingStyle = new NotificationCompat.MessagingStyle(user.getString(1));
                            messagingStyle.addMessage(intent.getStringExtra("msg"), System.currentTimeMillis(), (Person) null);
                            messageController.getMessagingStyleHashMap().put(user.getString(0), messagingStyle);
                        }
                        PendingIntent chatIntent;
                        try {
                             chatIntent=PendingIntent.getActivity(messageController.getContext(), messageController.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        }catch(NullPointerException e){
                            messageController.getPendingIds().put(intent.getStringExtra("idChat"),messageController.counter);
                            chatIntent=PendingIntent.getActivity(messageController.getContext(), messageController.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            messageController.counter++;
                        }

                        Intent replyIntent = new Intent(context, this.getClass());
                        replyIntent.putExtra("intentType","messageController");
                        replyIntent.putExtra("communicationType","reply");
                        replyIntent.putExtra("idChat",intent.getStringExtra("idChat"));
                        replyIntent.putExtra("username",user.getString(1));

                        // Build a PendingIntent for the reply action to trigger.
                        PendingIntent replyPendingIntent =
                                PendingIntent.getBroadcast(messageController.getContext(),
                                        messageController.getPendingIds().get(intent.getStringExtra("idChat")),
                                        replyIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                        // Create the reply action and add the remote input.
                        NotificationCompat.Action action =
                                new NotificationCompat.Action.Builder(R.drawable.ic_send,
                                        getContext().getString(R.string.reply_label), replyPendingIntent)
                                        .addRemoteInput(remoteInput)
                                        .setAllowGeneratedReplies(true)
                                        .build();


                        Bitmap bitmap = BitmapFactory.decodeFile(user.getString(user.getColumnIndex(Task.TaskEntry.PROFILE_PIC)));
                        Drawable draw = new BitmapDrawable(messageController.getContext().getResources(), bitmap);
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageController.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(messagingStyle)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                .setGroup("CHAT_GROUP")
                                .setContentIntent(chatIntent)
                                .addAction(action)
                                .setAutoCancel(true);

                        Notification summaryNotification = new NotificationCompat.Builder(messageController.getContext(), "chatMessageNotification")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setGroup("CHAT_GROUP")
                                        .setGroupSummary(true)
                                        .setAutoCancel(true)
                                        .build();
                        System.out.println(messageController.getPendingIds().get(intent.getStringExtra("idChat")));
                        notificationManager.notify(messageController.getPendingIds().get(intent.getStringExtra("idChat")), notificationBuilder.build());
                        notificationManager.notify(0, summaryNotification);
                        if (Connection.fragmentName.equals("chat")) {
                           messageController.chatAdapter.swapCursor(Connection.database.getAllChat());
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
                    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                    if (remoteInput != null) {
                        messageController.getTcpClient().sendMessage(String.valueOf(remoteInput.getCharSequence(KEY_TEXT_REPLY)),intent.getStringExtra("idChat"));
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageController.getContext());
                        notificationManager.cancel(messageController.getPendingIds().get(intent.getStringExtra("idChat")));
                        MessageController.getIstance().getMessagingStyleHashMap().replace(intent.getStringExtra("idChat"), new NotificationCompat.MessagingStyle(intent.getStringExtra("username")));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
