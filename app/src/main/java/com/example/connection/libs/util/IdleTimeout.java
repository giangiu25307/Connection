package com.example.connection.libs.util;

import android.os.Handler;

import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.util.TimeoutBase;

public class IdleTimeout extends TimeoutBase {
    Runnable callback;

    public IdleTimeout(AsyncServer server, long delay) {
        super(server, delay);

    }

    public IdleTimeout(Handler handler, long delay) {
        super(handler, delay);
    }

    public void setTimeout(Runnable callback) {
        this.callback = callback;
    }

    Object cancellable;
    public void reset() {
        handlerish.removeAllCallbacks(cancellable);
        cancellable = handlerish.postDelayed(callback, delay);
    }

    public void cancel() {
        // must post this, so that when it runs it removes everything in the queue,
        // preventing any rescheduling.
        // posting gaurantees there is not a reschedule in progress.
        handlerish.post(() -> handlerish.removeAllCallbacks(cancellable));
    }
}
