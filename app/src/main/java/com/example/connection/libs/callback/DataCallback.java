package com.example.connection.libs.callback;

import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;


public interface DataCallback {
    public class NullDataCallback implements DataCallback {
        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            bb.recycle();
        }
    }

    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb);
}
