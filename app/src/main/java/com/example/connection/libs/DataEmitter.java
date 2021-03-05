package com.example.connection.libs;

import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.DataCallback;

public interface DataEmitter {
    void setDataCallback(DataCallback callback);
    DataCallback getDataCallback();
    boolean isChunked();
    void pause();
    void resume();
    void close();
    boolean isPaused();
    void setEndCallback(CompletedCallback callback);
    CompletedCallback getEndCallback();
    AsyncServer getServer();
    String charset();
}
