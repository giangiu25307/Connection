package com.ConnectionProject.connection.libs.future;

public interface DependentCancellable extends com.ConnectionProject.connection.libs.future.Cancellable {
    boolean setParent(Cancellable parent);
}
