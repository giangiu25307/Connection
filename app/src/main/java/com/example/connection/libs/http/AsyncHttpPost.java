package com.example.connection.libs.http;

import android.net.Uri;

import com.example.connection.libs.http.AsyncHttpRequest;

public class AsyncHttpPost extends AsyncHttpRequest {
    public static final String METHOD = "POST";
    
    public AsyncHttpPost(String uri) {
        this(Uri.parse(uri));
    }

    public AsyncHttpPost(Uri uri) {
        super(uri, METHOD);
    }
}
