package com.example.connection.Controller;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.View.ChatActivity;
import com.example.connection.View.Connection;
import com.google.android.material.snackbar.Snackbar;

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

    private void checkNotificationChannelEnabled(){

    }

    private boolean isNotificationChannelEnabled(@Nullable String channelId) {
        if (!TextUtils.isEmpty(channelId)) {
            NotificationManager manager = (NotificationManager) messageController.context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = manager.getNotificationChannel(channelId);
            return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        return false;
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
                        if (!isNotificationChannelEnabled("chatMessageNotification") && !context.getSharedPreferences("utils", Context.MODE_PRIVATE).getBoolean("notificationsPopupShown", false)) {
                            Snackbar snackbar = Snackbar.make(((Activity) messageController.context).getWindow().getDecorView().getRootView(), "", 6000);
                            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                            layout.setBackgroundColor(messageController.context.getColor(R.color.transparent));
                            TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setVisibility(View.INVISIBLE);
                            View snackView = ((Activity) messageController.context).getLayoutInflater().inflate(R.layout.lyt_notification_snackbar, null);
                            ImageView imageView = snackView.findViewById(R.id.imageView12);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .putExtra(Settings.EXTRA_APP_PACKAGE, messageController.context.getString(R.string.packagename))
                                            .putExtra(Settings.EXTRA_CHANNEL_ID, "chatMessageNotification");
                                    messageController.context.startActivity(settingsIntent);
                                }
                            });
                            layout.setPadding(5, 5, 5, 5);
                            layout.addView(snackView, 0);
                            snackbar.show();
                            //messageController.context.getSharedPreferences("utils", Context.MODE_PRIVATE).edit().putBoolean("notificationsPopupShown", true).apply();
                        } else {
                            User user = Connection.database.getUser(intent.getStringExtra("idChat"));
                            chatActivity.putExtra("idChat", user.getIdUser());
                            chatActivity.putExtra("username", user.getUsername());
                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageController.getContext());
                            NotificationCompat.MessagingStyle messagingStyle;
                            if (messageController.getMessagingStyleHashMap().get(user.getIdUser()) != null) {
                                messagingStyle = messageController.getMessagingStyleHashMap().get(user.getIdUser());
                                messagingStyle.addMessage(intent.getStringExtra("msg"), System.currentTimeMillis(), (Person) null);
                                messageController.getMessagingStyleHashMap().replace(user.getIdUser(), messagingStyle);
                            } else {
                                messagingStyle = new NotificationCompat.MessagingStyle(user.getUsername());
                                messagingStyle.addMessage(intent.getStringExtra("msg"), System.currentTimeMillis(), (Person) null);
                                messageController.getMessagingStyleHashMap().put(user.getIdUser(), messagingStyle);
                            }
                            PendingIntent chatIntent;
                            try {
                                chatIntent = PendingIntent.getActivity(messageController.getContext(), messageController.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            } catch (NullPointerException e) {
                                messageController.getPendingIds().put(intent.getStringExtra("idChat"), messageController.counter);
                                chatIntent = PendingIntent.getActivity(messageController.getContext(), messageController.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                                messageController.counter++;
                            }

                            String replyLabel = messageController.getContext().getResources().getString(R.string.reply_label);
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
                                    PendingIntent.getBroadcast(messageController.getContext(),
                                            messageController.getPendingIds().get(intent.getStringExtra("idChat")),
                                            replyIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT);

                            // Create the reply action and add the remote input.
                            NotificationCompat.Action action =
                                    new NotificationCompat.Action.Builder(R.drawable.ic_send,
                                            messageController.getContext().getString(R.string.reply_label), replyPendingIntent)
                                            .addRemoteInput(remoteInput)
                                            .setAllowGeneratedReplies(true)
                                            .build();

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
                    }
                    break;
                case "multicast":
                    //check if i'm in global chat
                    if (Connection.isGlobalChatOpen) {

                    } else {
                        if (!isNotificationChannelEnabled("chatMessageNotification") && !context.getSharedPreferences("utils", Context.MODE_PRIVATE).getBoolean("notificationsPopupShown", false)) {
                            Snackbar snackbar = Snackbar.make(((Activity) context).getWindow().getDecorView(), "", 6000);
                            Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackbar.getView();
                            layout.setBackgroundColor(context.getColor(R.color.transparent));
                            TextView textView = layout.findViewById(com.google.android.material.R.id.snackbar_text);
                            textView.setVisibility(View.INVISIBLE);
                            View snackView = ((Activity) context).getLayoutInflater().inflate(R.layout.lyt_notification_snackbar, null);
                            ImageView imageView = snackView.findViewById(R.id.imageView12);
                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .putExtra(Settings.EXTRA_APP_PACKAGE, context.getString(R.string.packagename))
                                            .putExtra(Settings.EXTRA_CHANNEL_ID, "chatMessageNotification");
                                    context.startActivity(settingsIntent);
                                }
                            });
                            layout.setPadding(5, 5, 5, 5);
                            layout.addView(snackView, 0);
                            snackbar.show();
                            context.getSharedPreferences("utils", Context.MODE_PRIVATE).edit().putBoolean("notificationsPopupShown", true).apply();
                        }else {
                            //refresh chat
                        }
                    }
                    break;
                case "reply":
                    Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
                    if (remoteInput != null) {
                        //messageController.getTcpClient().sendMessage(String.valueOf(remoteInput.getCharSequence(KEY_TEXT_REPLY)),intent.getStringExtra("idChat"));
                        String reply = String.valueOf(remoteInput.getCharSequence(KEY_TEXT_REPLY));
                        System.out.println(reply);
                        NotificationCompat.MessagingStyle messagingStyle = messageController.getMessagingStyleHashMap().get(intent.getStringExtra("idChat"));
                        messagingStyle.addMessage(reply, System.currentTimeMillis(), (Person) null);
                        messageController.getMessagingStyleHashMap().put(intent.getStringExtra("idChat"), messagingStyle);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageController.getContext());

                        String replyLabel = messageController.getContext().getResources().getString(R.string.reply_label);
                        RemoteInput newRemoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                                .setLabel(replyLabel)
                                .build();

                        // Build a PendingIntent for the reply action to trigger.
                        PendingIntent replyPendingIntent =
                                PendingIntent.getBroadcast(messageController.getContext(),
                                        messageController.getPendingIds().get(intent.getStringExtra("idChat")),
                                        intent,
                                        PendingIntent.FLAG_UPDATE_CURRENT);

                        // Create the reply action and add the remote input.
                        NotificationCompat.Action action =
                                new NotificationCompat.Action.Builder(R.drawable.ic_send,
                                        messageController.getContext().getString(R.string.reply_label), replyPendingIntent)
                                        .addRemoteInput(newRemoteInput)
                                        .setAllowGeneratedReplies(true)
                                        .build();

                        PendingIntent chatIntent;
                        try {
                            chatIntent=PendingIntent.getActivity(messageController.getContext(), messageController.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        }catch(NullPointerException e){
                            messageController.getPendingIds().put(intent.getStringExtra("idChat"),messageController.counter);
                            chatIntent=PendingIntent.getActivity(messageController.getContext(), messageController.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                            messageController.counter++;
                        }

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

                        notificationManager.notify(messageController.getPendingIds().get(intent.getStringExtra("idChat")), notificationBuilder.build());
                        notificationManager.notify(0, summaryNotification);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
