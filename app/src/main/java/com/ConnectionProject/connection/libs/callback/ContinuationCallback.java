package com.ConnectionProject.connection.libs.callback;

import com.ConnectionProject.connection.libs.future.Continuation;

public interface ContinuationCallback {
    public void onContinue(Continuation continuation, CompletedCallback next) throws Exception;
}
