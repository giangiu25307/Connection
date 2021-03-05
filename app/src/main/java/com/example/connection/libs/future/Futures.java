package com.example.connection.libs.future;

import com.example.connection.libs.future.Future;
import com.example.connection.libs.future.FutureCallback;
import com.example.connection.libs.future.SimpleFuture;
import com.example.connection.libs.future.ThenFutureCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Futures {
    public static <T> com.example.connection.libs.future.Future<List<T>> waitAll(final List<com.example.connection.libs.future.Future<T>> futures) {
        final ArrayList<T> results = new ArrayList<>();
        final SimpleFuture<List<T>> ret = new SimpleFuture<>();

        if (futures.isEmpty()) {
            ret.setComplete(results);
            return ret;
        }

        FutureCallback<T> cb = new FutureCallback<T>() {
            int count = 0;

            @Override
            public void onCompleted(Exception e, T result) {
                results.add(result);
                count++;
                if (count < futures.size())
                    futures.get(count).setCallback(this);
                else
                    ret.setComplete(results);
            }
        };

        futures.get(0).setCallback(cb);

        return ret;
    }

    public static <T> com.example.connection.libs.future.Future<List<T>> waitAll(final com.example.connection.libs.future.Future<T>... futures) {
        return waitAll(Arrays.asList(futures));
    }


    private static <T, F> void loopUntil(final Iterator<F> values, ThenFutureCallback<T, F> callback, SimpleFuture<T> ret, Exception lastException) {
        while (values.hasNext()) {
            try {
                callback.then(values.next())
                        .success(ret::setComplete)
                        .fail(e -> loopUntil(values, callback, ret, e));
                return;
            } catch (Exception e) {
                lastException = e;
            }
        }

        if (lastException == null)
            ret.setComplete(new Exception("empty list"));
        else
            ret.setComplete(lastException);
    }

    public static <T, F> com.example.connection.libs.future.Future<T> loopUntil(final Iterable<F> values, ThenFutureCallback<T, F> callback) {
        SimpleFuture<T> ret = new SimpleFuture<>();
        loopUntil(values.iterator(), callback, ret, null);
        return ret;
    }

    public static <T, F> Future<T> loopUntil(final F[] values, ThenFutureCallback<T, F> callback) {
        return loopUntil(Arrays.asList(values), callback);
    }
}