package com.example.connection.libs.http.server;

import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.DataCallback;
import com.example.connection.libs.http.AsyncHttpRequest;
import com.example.connection.libs.http.body.AsyncHttpRequestBody;

public class UnknownRequestBody implements AsyncHttpRequestBody<Void> {
    public UnknownRequestBody(String contentType) {
        mContentType = contentType;
    }

    int length = -1;
    public UnknownRequestBody(DataEmitter emitter, String contentType, int length) {
        mContentType = contentType;
        this.emitter = emitter;
        this.length = length;
    }

    @Override
    public void write(final AsyncHttpRequest request, DataSink sink, final CompletedCallback completed) {
        Util.pump(emitter, sink, completed);
        if (emitter.isPaused())
            emitter.resume();
    }

    private String mContentType;
    @Override
    public String getContentType() {
        return mContentType;
    }

    @Override
    public boolean readFullyOnRequest() {
        return false;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public Void get() {
        return null;
    }

    @Deprecated
    public void setCallbacks(DataCallback callback, CompletedCallback endCallback) {
        emitter.setEndCallback(endCallback);
        emitter.setDataCallback(callback);
    }

    public DataEmitter getEmitter() {
        return emitter;
    }

    DataEmitter emitter;
    @Override
    public void parse(DataEmitter emitter, CompletedCallback completed) {
        this.emitter = emitter;
        emitter.setEndCallback(completed);
        emitter.setDataCallback(new DataCallback.NullDataCallback());
    }
}
