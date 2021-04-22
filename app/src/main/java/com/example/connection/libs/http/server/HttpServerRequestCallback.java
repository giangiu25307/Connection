package com.example.connection.libs.http.server;


import com.example.connection.libs.http.server.AsyncHttpServerRequest;
import com.example.connection.libs.http.server.AsyncHttpServerResponse;

public interface HttpServerRequestCallback {
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
