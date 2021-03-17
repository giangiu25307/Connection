package com.example.connection.libs.callback;

public interface CompletedCallback {
    public class NullCompletedCallback implements CompletedCallback {
        @Override
        public void onCompleted(Exception ex) {

        }
    }

    public void onCompleted(Exception ex);
}