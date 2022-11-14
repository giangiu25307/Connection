package com.ConnectionProject.connection.libs.http.server;


public interface HttpServerRequestCallback {
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response);
}
