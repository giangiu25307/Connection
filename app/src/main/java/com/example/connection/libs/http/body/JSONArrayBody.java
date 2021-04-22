package com.example.connection.libs.http.body;

import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.future.FutureCallback;
import com.example.connection.libs.http.AsyncHttpRequest;
import com.example.connection.libs.http.body.AsyncHttpRequestBody;
import com.example.connection.libs.parser.JSONArrayParser;

import org.json.JSONArray;

public class JSONArrayBody implements AsyncHttpRequestBody<JSONArray> {
    public JSONArrayBody() {
    }

    byte[] mBodyBytes;
    JSONArray json;
    public JSONArrayBody(JSONArray json) {
        this();
        this.json = json;
    }

    @Override
    public void parse(DataEmitter emitter, final CompletedCallback completed) {
        new JSONArrayParser().parse(emitter).setCallback(new FutureCallback<JSONArray>() {
            @Override
            public void onCompleted(Exception e, JSONArray result) {
                json = result;
                completed.onCompleted(e);
            }
        });
    }

    @Override
    public void write(AsyncHttpRequest request, DataSink sink, final CompletedCallback completed) {
        Util.writeAll(sink, mBodyBytes, completed);
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public boolean readFullyOnRequest() {
        return true;
    }

    @Override
    public int length() {
        mBodyBytes = json.toString().getBytes();
        return mBodyBytes.length;
    }

    public static final String CONTENT_TYPE = "application/json";

    @Override
    public JSONArray get() {
        return json;
    }
}

