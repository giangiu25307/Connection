package com.example.connection.libs;

import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.WritableCallback;

public interface DataSink {
    public void write(ByteBufferList bb);
    public void setWriteableCallback(WritableCallback handler);
    public WritableCallback getWriteableCallback();
    
    public boolean isOpen();
    public void end();
    public void setClosedCallback(CompletedCallback handler);
    public CompletedCallback getClosedCallback();
    public AsyncServer getServer();
}
