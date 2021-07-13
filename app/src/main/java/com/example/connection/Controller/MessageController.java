package com.example.connection.Controller;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.R;
import com.example.connection.Services.MyNotificationService;
import com.example.connection.View.Connection;

import java.util.HashMap;

public class MessageController extends BroadcastReceiver {

    public MessageAdapter messageAdapter;
    private static MessageController messageController;
    private ChatAdapter chatAdapter;
    private Context context;
    private HashMap<String, NotificationCompat.MessagingStyle> messagingStyleHashMap;

    public static MessageController newInstance(Context context) {
        messageController = new MessageController();
        messageController.setContext(context);
        messageController.setMessagingStyleHashMap();
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

    public void setMessagingStyleHashMap() {
        this.messagingStyleHashMap = new HashMap<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getStringExtra("intentType").equals("messageController")) {
            switch (intent.getStringExtra("communicationType")) {
                case "tcp":
                    if (Connection.idChatOpen.equals(intent.getStringExtra("id"))) {
                        messageController.messageAdapter.swapCursor(Connection.database.getAllMsg(intent.getStringExtra("id")));
                    } else {
                        Cursor user = Connection.database.getUser(intent.getStringExtra("id"));
                        user.moveToFirst();
                        MyNotificationService myNotificationService =
                                new MyNotificationService(user.getString(user.getColumnIndex(Task.TaskEntry.NAME)),
                                        intent.getStringExtra("msg"),user.getString(user.getColumnIndex(Task.TaskEntry.PROFILE_PIC)));

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

                        Bitmap bitmap = BitmapFactory.decodeFile(user.getString(user.getColumnIndex(Task.TaskEntry.PROFILE_PIC)));
                        Drawable draw = new BitmapDrawable(getContext().getResources(), bitmap);
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageController.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(messagingStyle)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                //.setContentIntent(pendingIntent)
                                //.addAction(R.drawable.ic_snooze, getString(R.string.snooze), snoozePendingIntent)
                                .setAutoCancel(true);
                        System.out.println("notifica creata");
                        notificationManager.notify(1, notificationBuilder.build());
                        if (Connection.fragmentName.equals("chat")) {
                           messageController.chatAdapter.swapCursor(Connection.database.getAllChat());
                        }
                    }
                    break;
                case "multicast":
                    //check if i'm in global chat
                    if (!Connection.isGlobalChatOpen) {
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageController.getContext());
                        long prova = 0;
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageController.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.drawable.andrew_profile_icon)
                                .setStyle(new NotificationCompat.MessagingStyle("Me")
                                        .addMessage("Hi", prova, (Person) null)) // Pass in null for user.
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                //.setContentIntent(pendingIntent)
                                //.addAction(R.drawable.ic_snooze, getString(R.string.snooze), snoozePendingIntent)
                                .setAutoCancel(true);
                        System.out.println("notifica creata");
                        notificationManager.notify(1, notificationBuilder.build());
                    } else {
                        //refresh chat
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
