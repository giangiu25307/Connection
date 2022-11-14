package com.ConnectionProject.connection.libs.callback;

import com.ConnectionProject.connection.libs.AsyncServerSocket;
import com.ConnectionProject.connection.libs.AsyncSocket;


public interface ListenCallback extends CompletedCallback {
    public void onAccepted(AsyncSocket socket);
    public void onListening(AsyncServerSocket socket);
}
