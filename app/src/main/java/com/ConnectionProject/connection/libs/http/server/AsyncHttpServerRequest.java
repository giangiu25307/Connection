package com.ConnectionProject.connection.libs.http.server;

import com.ConnectionProject.connection.libs.AsyncSocket;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.http.Headers;
import com.ConnectionProject.connection.libs.http.Multimap;
import com.ConnectionProject.connection.libs.http.body.AsyncHttpRequestBody;

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
