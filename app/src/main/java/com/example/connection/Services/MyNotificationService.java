package com.example.connection.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.example.connection.R;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MyNotificationService extends Service {

    private String msg,name,path;
    private Person person;
    private ArrayList<NotificationCompat.MessagingStyle.Message> messages;

    public MyNotificationService(String msg, String name, String path) {
        this.msg=msg;
        this.name=name;
        this.path=path;
        messages=new ArrayList<>();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCreate() {
        super.onCreate();
        CharSequence name = "Chat message";
        person = new Person.Builder()
                .setName(this.name)
                .setIcon(IconCompat.createFromIcon(Icon.createWithFilePath(path)))
                .build();
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("chatMessageNotification", name, importance);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.MessagingStyle.Message message =
                new NotificationCompat.MessagingStyle.Message(msg,
                        System.currentTimeMillis(),
                        person);
    }

}
