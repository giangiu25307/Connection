package com.ConnectionProject.connection.libs.http.body;

import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.DataSink;
import com.ConnectionProject.connection.libs.Util;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.http.AsyncHttpRequest;
import com.ConnectionProject.connection.libs.parser.ByteBufferListParser;

public class ByteBufferListRequestBody implements AsyncHttpRequestBody<ByteBufferList> {
    public ByteBufferListRequestBody() {
    }

    ByteBufferList bb;
    public ByteBufferListRequestBody(ByteBufferList bb) {
        this.bb = bb;
    }
    @Override
    public void write(AsyncHttpRequest request, DataSink sink, CompletedCallback completed) {
        Util.writeAll(sink, bb, completed);
    }

    @Override
    public void parse(DataEmitter emitter, CompletedCallback completed) {
        new ByteBufferListParser().parse(emitter).setCallback((e, result) -> {
            bb = result;
            completed.onCompleted(e);
        });
    }

    public static String CONTENT_TYPE = "application/binary";

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public boolean readFullyOnRequest() {
        return true;
    }

    @Override
    public int length() {
        return bb.remaining();
    }

    @Override
    public ByteBufferList get() {
        return bb;
    }
}
