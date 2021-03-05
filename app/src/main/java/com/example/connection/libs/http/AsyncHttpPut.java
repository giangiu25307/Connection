package com.example.connection.libs.http;

import android.net.Uri;

import com.example.connection.libs.http.AsyncHttpRequest;

public class AsyncHttpPut extends AsyncHttpRequest {
    public static final String METHOD = "PUT";
    
    public AsyncHttpPut(String uri) {
        this(Uri.parse(uri));
    }

    public AsyncHttpPut(Uri uri) {
        super(uri, METHOD);
    }
}
