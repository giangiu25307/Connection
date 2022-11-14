package com.ConnectionProject.connection.libs.callback;

import com.ConnectionProject.connection.libs.AsyncSocket;

public interface ConnectCallback {
    public void onConnectCompleted(Exception ex, AsyncSocket socket);
}
