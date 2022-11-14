package com.ConnectionProject.connection.libs.wrapper;

import com.ConnectionProject.connection.libs.AsyncSocket;

public interface AsyncSocketWrapper extends AsyncSocket, DataEmitterWrapper {
    public AsyncSocket getSocket();
}
