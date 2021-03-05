package com.example.connection.libs.future;

public interface SuccessCallback<T> {
    void success(T value) throws Exception;
}
