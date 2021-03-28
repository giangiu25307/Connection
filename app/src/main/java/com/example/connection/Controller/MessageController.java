package com.example.connection.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.Services.MyNotificationService;
import com.example.connection.View.Connection;

public class MessageController extends BroadcastReceiver {

    private MessageAdapter messageAdapter;
    private static MessageController messageController;
    private Database database;
    private ChatAdapter chatAdapter;

    public static MessageController newInstance(Database database) {
        messageController = new MessageController();
        messageController.setDatabase(database);
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

    public void setDatabase(Database database) {
        this.database = database;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getStringExtra("intentType").equals("messageController")) {
            switch (intent.getStringExtra("communicationType")) {
                case "tcp":
                    if (Connection.idChatOpen.equals(intent.getStringExtra("id"))) {
                        messageAdapter.swapCursor(database.getAllMsg(intent.getStringExtra("id")));
                    } else {
                        Cursor user = database.getUser(intent.getStringExtra("id"));
                        MyNotificationService myNotificationService = new MyNotificationService(user.getString(user.getColumnIndex(Task.TaskEntry.NAME)),intent.getStringExtra("msg"),user.getString(user.getColumnIndex(Task.TaskEntry.PROFILE_PIC)));
                        Intent notificationIntent = new Intent(context, myNotificationService.getClass());
                        context.startForegroundService(notificationIntent);
                        if (Connection.fragmentName.equals("chat")) {
                            chatAdapter.swapCursor(database.getAllChat());//chiedere a bergo se sto aggiornando col cursore giusto
                        }
                    }
                    break;
                case "multicast":
                    //check if i'm in global chat
                    if (!Connection.isGlobalChatOpen) {
                        //create notification here
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
