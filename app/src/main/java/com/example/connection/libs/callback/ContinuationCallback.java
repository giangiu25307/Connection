package com.example.connection.libs.callback;

import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.future.Continuation;

public interface ContinuationCallback {
    public void onContinue(Continuation continuation, CompletedCallback next) throws Exception;
}
