package com.example.connection.libs.http.server;

import com.example.connection.libs.http.Headers;
import com.example.connection.libs.http.body.AsyncHttpRequestBody;

public interface AsyncHttpRequestBodyProvider {
    AsyncHttpRequestBody getBody(Headers headers);
}
