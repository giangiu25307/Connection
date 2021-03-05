package com.example.connection.libs.http.server;

import com.example.connection.libs.AsyncSocket;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.http.Headers;
import com.example.connection.libs.http.Multimap;
import com.example.connection.libs.http.body.AsyncHttpRequestBody;

import java.util.Map;
import java.util.regex.Matcher;

public interface AsyncHttpServerRequest extends DataEmitter {
    Headers getHeaders();
    Matcher getMatcher();
    void setMatcher(Matcher matcher);
    <T extends AsyncHttpRequestBody> T getBody();
    AsyncSocket getSocket();
    String getPath();
    Multimap getQuery();
    String getMethod();
    String getUrl();

    String get(String name);
    Map<String, Object> getState();
}
