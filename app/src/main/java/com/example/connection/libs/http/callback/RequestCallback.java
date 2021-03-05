package com.example.connection.libs.http.callback;

import com.example.connection.libs.callback.ResultCallback;
import com.example.connection.libs.http.AsyncHttpResponse;

public interface RequestCallback<T> extends ResultCallback<AsyncHttpResponse, T> {
    public void onConnect(AsyncHttpResponse response);
    public void onProgress(AsyncHttpResponse response, long downloaded, long total);
}
