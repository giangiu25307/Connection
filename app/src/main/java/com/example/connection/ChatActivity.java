package com.example.connection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.example.connection.Adapter.ChatAdapter;
import com.example.connection.Adapter.MessageAdapter;
import com.example.connection.Controller.Database;

import java.io.Serializable;

public class ChatActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        String id = getIntent().getStringExtra("idChat");
        //Database database = (Database) getIntent().getParcelableExtra("database");
        Database database = new Database(this);
        setupRecyclerView(database, id);

    }

    private void setupRecyclerView(Database database, String id){
        RecyclerView recyclerView = findViewById(R.id.messageRecyclerView);
        MessageAdapter chatAdapter = new MessageAdapter(this, database, id);
        recyclerView.setAdapter(chatAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

}