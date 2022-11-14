package com.ConnectionProject.connection.libs.http.server;

import com.ConnectionProject.connection.libs.http.Headers;
import com.ConnectionProject.connection.libs.http.body.AsyncHttpRequestBody;

public interface AsyncHttpRequestBodyProvider {
    AsyncHttpRequestBody getBody(Headers headers);
}
