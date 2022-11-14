package com.ConnectionProject.connection.libs.callback;

import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataEmitter;


public interface DataCallback {
    public class NullDataCallback implements DataCallback {
        @Override
        public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
            bb.recycle();
        }
    }

    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb);
}
