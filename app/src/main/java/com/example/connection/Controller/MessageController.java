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

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.R;
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
                            int id=0;
                            for (int i=0;i<intent.getStringExtra("idChat").length();i++){
                                id+=intent.getStringExtra("idChat").charAt(i);
                            }
                            messageController.getPendingIds().put(intent.getStringExtra("idChat"),id);
                            chatIntent=PendingIntent.getActivity(messageController.getContext(), id, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(user.getString(user.getColumnIndex(Task.TaskEntry.PROFILE_PIC)));
                        Drawable draw = new BitmapDrawable(messageController.getContext().getResources(), bitmap);
                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageController.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(messagingStyle)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                .setGroup("CHAT_GROUP")
                                .setContentIntent(chatIntent)
                                //.addAction(R.drawable.ic_snooze, getString(R.string.snooze), snoozePendingIntent)
                                .setAutoCancel(true);

                        Notification summaryNotification = new NotificationCompat.Builder(context, "chatMessageNotification")
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setGroup("CHAT_GROUP")
                                        .setGroupSummary(true)
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
                default:
                    break;
            }
        }
    }
}
