package com.example.connection.libs.callback;

import com.example.connection.libs.AsyncSocket;

public interface ConnectCallback {
    public void onConnectCompleted(Exception ex, AsyncSocket socket);
}
