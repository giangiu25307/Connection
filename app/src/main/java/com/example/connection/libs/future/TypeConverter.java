package com.example.connection.libs.future;

import com.example.connection.libs.future.Future;

public interface TypeConverter<T, F> {
    Future<T> convert(F from, String fromMime) throws Exception;
}
