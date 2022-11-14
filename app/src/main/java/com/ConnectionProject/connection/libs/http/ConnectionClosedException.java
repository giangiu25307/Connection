package com.ConnectionProject.connection.libs.http;

public class ConnectionClosedException extends Exception {
    public ConnectionClosedException(String message) {
        super(message);
    }

    public ConnectionClosedException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
