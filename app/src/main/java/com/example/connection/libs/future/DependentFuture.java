package com.example.connection.libs.future;

import com.example.connection.libs.future.DependentCancellable;
import com.example.connection.libs.future.Future;

public interface DependentFuture<T> extends Future<T>, DependentCancellable {
}
