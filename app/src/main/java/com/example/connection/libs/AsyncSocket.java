package com.example.connection.libs;


import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;

public interface AsyncSocket extends DataEmitter, DataSink {
    public AsyncServer getServer();
}
