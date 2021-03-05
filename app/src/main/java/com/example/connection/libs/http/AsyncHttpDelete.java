package com.example.connection.libs.http;

import android.net.Uri;

import com.example.connection.libs.http.AsyncHttpRequest;

public class AsyncHttpDelete extends AsyncHttpRequest {
    public static final String METHOD = "DELETE";

    public AsyncHttpDelete(String uri) {
        this(Uri.parse(uri));
    }

    public AsyncHttpDelete(Uri uri) {
        super(uri, METHOD);
    }
}
