package com.example.connection.libs.future;

import com.example.connection.libs.future.Cancellable;

public interface DependentCancellable extends com.example.connection.libs.future.Cancellable {
    boolean setParent(Cancellable parent);
}
