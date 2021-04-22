package com.example.connection.libs.http;

import com.example.connection.libs.AsyncSocket;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.http.AsyncHttpRequest;
import com.example.connection.libs.http.Headers;

public interface AsyncHttpResponse extends DataEmitter {
    public String protocol();
    public String message();
    public int code();
    public Headers headers();
    public AsyncSocket detachSocket();
    public AsyncHttpRequest getRequest();
}
