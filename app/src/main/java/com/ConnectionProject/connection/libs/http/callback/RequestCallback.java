package com.ConnectionProject.connection.libs.http.callback;

import com.ConnectionProject.connection.libs.callback.ResultCallback;
import com.ConnectionProject.connection.libs.http.AsyncHttpResponse;

public interface RequestCallback<T> extends ResultCallback<AsyncHttpResponse, T> {
    public void onConnect(AsyncHttpResponse response);
    public void onProgress(AsyncHttpResponse response, long downloaded, long total);
}
