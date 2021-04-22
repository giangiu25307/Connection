package com.example.connection.libs.wrapper;

import com.example.connection.libs.AsyncSocket;
import com.example.connection.libs.wrapper.DataEmitterWrapper;

public interface AsyncSocketWrapper extends AsyncSocket, DataEmitterWrapper {
    public AsyncSocket getSocket();
}
