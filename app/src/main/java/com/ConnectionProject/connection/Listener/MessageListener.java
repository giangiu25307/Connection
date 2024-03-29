package com.ConnectionProject.connection.Listener;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.Person;
import androidx.core.app.RemoteInput;

import com.ConnectionProject.connection.Adapter.ChatAdapter;
import com.ConnectionProject.connection.Adapter.GlobalMessageAdapter;
import com.ConnectionProject.connection.Adapter.MessageAdapter;
import com.ConnectionProject.connection.Controller.ChatController;
import com.ConnectionProject.connection.Controller.ConnectionController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Model.GlobalMessage;
import com.ConnectionProject.connection.Model.LastMessage;
import com.ConnectionProject.connection.Model.Message;
import com.ConnectionProject.connection.Model.User;
import com.ConnectionProject.connection.R;
import com.ConnectionProject.connection.SFTP.SFTPClient;
import com.ConnectionProject.connection.SFTP.SFTPServer;
import com.ConnectionProject.connection.TCP_Connection.Encryption;
import com.ConnectionProject.connection.TCP_Connection.TcpClient;
import com.ConnectionProject.connection.UDP_Connection.MyNetworkInterface;
import com.ConnectionProject.connection.View.ChatActivity;
import com.ConnectionProject.connection.View.ChatFragment;
import com.ConnectionProject.connection.View.Connection;
import com.ConnectionProject.connection.libs.AsyncServer;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

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
    private TcpClient tcpClient;
    private Encryption encryption;

    // Key for the string that's delivered in the action's intent.
    private static final String KEY_TEXT_REPLY = "key_text_reply";

    public static MessageListener newInstance(Context context, Database database, ChatController chatController, TcpClient tcpClient, Encryption encryption) {
        messageListener = new MessageListener();
        messageListener.setContext(context);
        messageListener.setMessagingStyleHashMap();
        messageListener.setPendingIds();
        messageListener.setDatabase(database);
        messageListener.setChatController(chatController);
        messageListener.setTcpClient(tcpClient);
        messageListener.setEncryption(encryption);
        return messageListener;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
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

    public void setTcpClient(TcpClient tcpClient) {
        this.tcpClient = tcpClient;
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

    /**
     * Listening for message which information is inside of the intent
     *
     * @param context context of the application
     * @param intent  contains message information
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent chatActivity = new Intent(messageListener.getContext(), ChatActivity.class);
        messageListener = MessageListener.getIstance();
        if (intent.getStringExtra("intentType").equals("messageController")) {
            switch (intent.getStringExtra("communicationType")) {
                case "tcp":
                    ChatFragment.getIstance().setTotalRequest(messageListener.database.getAllRequestChat().getCount());
                    ChatFragment.getIstance().setTotalChatNumber(messageListener.database.getAllNoRequestChat().getCount());
                    Connection.isNewMessageArrived = true;
                    if (Connection.idChatOpen.equals(intent.getStringExtra("idChat"))) {
                        LastMessage lastMessage = messageListener.database.getLastMessageChat(intent.getStringExtra("idChat"));
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        Date date = new Date();
                        try {
                            date = format.parse(lastMessage.getDateTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        messageListener.messageAdapter.addMessage(new Message(messageListener.database.getLastMessageId(intent.getStringExtra("idChat")), intent.getStringExtra("idUser"), lastMessage.getLastMessage(), date.toString(), intent.getStringExtra("sent")));
                        messageListener.database.setReadAllChatMessages(intent.getStringExtra("idChat"));
                    } else if (!messageListener.database.isUserBlocked(intent.getStringExtra("idChat"))) {
                        if (Connection.fragmentName.equals("CHAT")) {
                            ChatFragment chatFragment = ChatFragment.getIstance();
                            chatFragment.setupRecyclerView(chatFragment.requireView());
                        }
                        if (Connection.isRequestDialogOpen) {
                            ChatFragment.setupRequestRecyclerView();
                        }
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
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } catch (NullPointerException e) {
                            messageListener.getPendingIds().put(intent.getStringExtra("idChat"), messageListener.counter);
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
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

                        Intent markAsReadIntent = new Intent(context, this.getClass());
                        markAsReadIntent.putExtra("intentType", "messageController");
                        markAsReadIntent.putExtra("communicationType", "markAsRead");
                        markAsReadIntent.putExtra("idChat", intent.getStringExtra("idChat"));

                        // Build a PendingIntent for the reply action to trigger.
                        PendingIntent replyPendingIntent =
                                PendingIntent.getBroadcast(messageListener.getContext(),
                                        messageListener.getPendingIds().get(intent.getStringExtra("idChat")),
                                        replyIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

                        PendingIntent markAsReadPendingIntent =
                                PendingIntent.getBroadcast(messageListener.getContext(),
                                        messageListener.getPendingIds().get(intent.getStringExtra("idChat")),
                                        markAsReadIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

                        // Create the reply action and add the remote input.
                        NotificationCompat.Action replyAction =
                                new NotificationCompat.Action.Builder(R.drawable.ic_send_from_notification,
                                        messageListener.getContext().getString(R.string.reply_label), replyPendingIntent)
                                        .addRemoteInput(remoteInput)
                                        .setAllowGeneratedReplies(true)
                                        .build();

                        // Create the mark as read action and add the remote input.
                        NotificationCompat.Action markAsReadAction =
                                new NotificationCompat.Action.Builder(null, messageListener.getContext().getString(R.string.mark_as_raed_label), markAsReadPendingIntent)
                                        .build();

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(messageListener.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setStyle(messagingStyle)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                .setGroup("CHAT_GROUP")
                                .setContentIntent(chatIntent)
                                .addAction(replyAction)
                                .addAction(markAsReadAction)
                                .setAutoCancel(true);

                        Notification summaryNotification = new NotificationCompat.Builder(messageListener.getContext(), "chatMessageNotification")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setGroup("CHAT_GROUP")
                                .setGroupSummary(true)
                                .setAutoCancel(true)
                                .build();
                        notificationManager.notify(messageListener.getPendingIds().get(intent.getStringExtra("idChat")), notificationBuilder.build());
                        notificationManager.notify(0, summaryNotification);
                        if (Connection.fragmentName.equals("CHAT")) {
                            messageListener.chatAdapter.swapCursor(Connection.database.getAllChat());
                        }
                    }
                    break;
                case "multicast":
                    //check if i'm in global chat
                    if (Connection.isGlobalChatOpen) {
                        messageListener.globalMessageAdapter.addMessage(new GlobalMessage(intent.getStringExtra("idMessage"), intent.getStringExtra("idUser"),
                                intent.getStringExtra("msg"), intent.getStringExtra("data"), intent.getStringExtra("username")));
                    }
                    break;
                case "reply":
                    messageListener.database.setReadMessage(intent.getStringExtra("idChat"), messageListener.database.getLastMessageId(intent.getStringExtra("idChat")));
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
                                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

                        // Create the reply action and add the remote input.
                        NotificationCompat.Action action =
                                new NotificationCompat.Action.Builder(R.drawable.ic_send_from_notification,
                                        messageListener.getContext().getString(R.string.reply_label), replyPendingIntent)
                                        .addRemoteInput(newRemoteInput)
                                        .setAllowGeneratedReplies(true)
                                        .build();

                        PendingIntent chatIntent;
                        try {
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.getPendingIds().get(intent.getStringExtra("idChat")), chatActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
                        } catch (NullPointerException e) {
                            messageListener.getPendingIds().put(intent.getStringExtra("idChat"), messageListener.counter);
                            chatIntent = PendingIntent.getActivity(messageListener.getContext(), messageListener.counter, chatActivity, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_MUTABLE);
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
                case "markAsRead":
                    messageListener.database.setReadAllMessages(intent.getStringExtra("idChat"));
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(messageListener.getContext());
                    notificationManager.cancel(messageListener.getPendingIds().get(intent.getStringExtra("idChat")));
                    if (Connection.fragmentName.equals("CHAT")) {
                        messageListener.chatAdapter.hideUnreadMessageCount(intent.getStringExtra("idChat"));
                    }
                    break;
                case "sendImage":
                    try {

                        Cursor c = messageListener.database.getAllUsersWithoutME();
                        while (!c.isAfterLast()) {
                            User user = new User(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7), c.getString(8), c.getString(9), c.getString(10));
                            messageListener.tcpClient.handShake(user.getIdUser(), c.getString(11), "");
                            while(messageListener.database.getSymmetricKey(c.getString(0)) == null);
                            String[] params = new String[4];
                            String ip = messageListener.database.findIp(user.getIdUser());
                            params[0] = ip;
                            params[1] = ConnectionController.myUser.getIdUser();
                            params[2] = messageListener.encryption.encryptAES(
                                    ConnectionController.myUser.getProfilePicBase64()
                                    ,messageListener.encryption.convertStringToSecretKey(messageListener.database.getSymmetricKey(c.getString(0)))
                            );
                            params[3] = ConnectionController.myUser.getProfilePic().split("\\.")[ConnectionController.myUser.getProfilePic().split("\\.").length - 1];
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    new SFTPClient(params);
                                }
                            }).start();
                            //messageListener.tcpClient.sendMyImageToEveryone(user, messageListener.database.getSymmetricKey(c.getString(0)));
                            c.moveToNext();
                        }
                    }  catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
