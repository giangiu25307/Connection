package com.example.connection.libs.callback;

import com.example.connection.libs.AsyncServerSocket;
import com.example.connection.libs.AsyncSocket;
import com.example.connection.libs.callback.CompletedCallback;


public interface ListenCallback extends CompletedCallback {
    public void onAccepted(AsyncSocket socket);
    public void onListening(AsyncServerSocket socket);
}
