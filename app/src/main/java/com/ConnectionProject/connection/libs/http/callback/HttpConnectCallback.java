package com.ConnectionProject.connection.libs.http.callback;


import com.ConnectionProject.connection.libs.http.AsyncHttpResponse;

public interface HttpConnectCallback {
    public void onConnectCompleted(Exception ex, AsyncHttpResponse response);
}
