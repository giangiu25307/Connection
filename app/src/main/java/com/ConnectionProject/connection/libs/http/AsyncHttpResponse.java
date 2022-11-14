package com.ConnectionProject.connection.libs.http;

import com.ConnectionProject.connection.libs.AsyncSocket;
import com.ConnectionProject.connection.libs.DataEmitter;

public interface AsyncHttpResponse extends DataEmitter {
    public String protocol();
    public String message();
    public int code();
    public Headers headers();
    public AsyncSocket detachSocket();
    public AsyncHttpRequest getRequest();
}
