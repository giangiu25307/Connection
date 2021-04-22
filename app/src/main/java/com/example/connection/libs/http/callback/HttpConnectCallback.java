package com.example.connection.libs.http.callback;


import com.example.connection.libs.http.AsyncHttpResponse;

public interface HttpConnectCallback {
    public void onConnectCompleted(Exception ex, AsyncHttpResponse response);
}
