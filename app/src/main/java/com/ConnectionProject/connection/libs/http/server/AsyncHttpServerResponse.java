package com.ConnectionProject.connection.libs.http.server;

import com.ConnectionProject.connection.libs.AsyncSocket;
import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataSink;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.http.AsyncHttpResponse;
import com.ConnectionProject.connection.libs.http.Headers;
import com.ConnectionProject.connection.libs.parser.AsyncParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface AsyncHttpServerResponse extends DataSink, CompletedCallback {
    void end();
    void send(String contentType, byte[] bytes);
    void send(String contentType, ByteBufferList bb);
    void send(String contentType, ByteBuffer bb);
    void send(String contentType, String string);
    void send(String string);
    void send(JSONObject json);
    void send(JSONArray jsonArray);
    void sendFile(File file);
    void sendStream(InputStream inputStream, long totalLength);
    <T> void sendBody(AsyncParser<T> body, T value);
    AsyncHttpServerResponse code(int code);
    int code();
    Headers getHeaders();
    void writeHead();
    void setContentType(String contentType);
    void redirect(String location);
    AsyncHttpServerRequest getRequest();
    String getHttpVersion();
    void setHttpVersion(String httpVersion);

    // NOT FINAL
    void proxy(AsyncHttpResponse response);

    /**
     * Alias for end. Used with CompletedEmitters
     */
    void onCompleted(Exception ex);
    AsyncSocket getSocket();
    void setSocket(AsyncSocket socket);
}
