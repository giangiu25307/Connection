package com.ConnectionProject.connection.libs.http;

import android.net.Uri;

public class AsyncHttpGet extends AsyncHttpRequest {
    public static final String METHOD = "GET";
    
    public AsyncHttpGet(String uri) {
        super(Uri.parse(uri), METHOD);
    }

    public AsyncHttpGet(Uri uri) {
        super(uri, METHOD);
    }
}
