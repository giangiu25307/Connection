package com.example.connection.libs;

import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.callback.DataCallback;

public class DataEmitterReader implements DataCallback {
    DataCallback mPendingRead;
    int mPendingReadLength;
    com.example.connection.libs.ByteBufferList mPendingData = new com.example.connection.libs.ByteBufferList();

    public void read(int count, DataCallback callback) {
        mPendingReadLength = count;
        mPendingRead = callback;
        mPendingData.recycle();
    }

    private boolean handlePendingData(com.example.connection.libs.DataEmitter emitter) {
        if (mPendingReadLength > mPendingData.remaining())
            return false;

        DataCallback pendingRead = mPendingRead;
        mPendingRead = null;
        pendingRead.onDataAvailable(emitter, mPendingData);

        return true;
    }

    public DataEmitterReader() {
    }
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
        // if we're registered for data, we must be waiting for a read
        do {
            int need = Math.min(bb.remaining(), mPendingReadLength - mPendingData.remaining());
            bb.get(mPendingData, need);
            bb.remaining();
        }
        while (handlePendingData(emitter) && mPendingRead != null);
        bb.remaining();
    }
}
