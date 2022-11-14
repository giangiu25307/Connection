package com.ConnectionProject.connection.libs.future;

public interface DoneCallback<T> {
    void done(Exception e, T result) throws Exception;
}
