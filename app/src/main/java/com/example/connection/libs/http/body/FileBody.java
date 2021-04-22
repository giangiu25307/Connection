package com.example.connection.libs.http.body;

import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.http.AsyncHttpRequest;
import com.example.connection.libs.http.body.AsyncHttpRequestBody;

import java.io.File;

/**
 * Created by koush on 10/14/13.
 */
public class FileBody implements AsyncHttpRequestBody<File> {
    public static final String CONTENT_TYPE = "application/binary";

    File file;
    String contentType = CONTENT_TYPE;

    public FileBody(File file) {
        this.file = file;
    }

    public FileBody(File file, String contentType) {
        this.file = file;
        this.contentType = contentType;
    }

    @Override
    public void write(AsyncHttpRequest request, DataSink sink, CompletedCallback completed) {
        Util.pump(file, sink, completed);
    }

    @Override
    public void parse(DataEmitter emitter, CompletedCallback completed) {
        throw new AssertionError("not implemented");
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean readFullyOnRequest() {
        throw new AssertionError("not implemented");
    }

    @Override
    public int length() {
        return (int)file.length();
    }

    @Override
    public File get() {
        return file;
    }
}
