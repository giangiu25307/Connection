package com.ConnectionProject.connection.libs.http;

public class RedirectLimitExceededException extends Exception {
    public RedirectLimitExceededException(String message) {
        super(message);
    }
}
