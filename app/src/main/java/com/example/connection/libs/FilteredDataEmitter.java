package com.example.connection.libs;

import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataEmitterBase;
import com.example.connection.libs.DataTrackingEmitter;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.DataCallback;
import com.example.connection.libs.wrapper.DataEmitterWrapper;

public class FilteredDataEmitter extends DataEmitterBase implements com.example.connection.libs.DataEmitter, DataCallback, DataEmitterWrapper, DataTrackingEmitter {
    private com.example.connection.libs.DataEmitter mEmitter;
    @Override
    public com.example.connection.libs.DataEmitter getDataEmitter() {
        return mEmitter;
    }

    @Override
    public void setDataEmitter(com.example.connection.libs.DataEmitter emitter) {
        if (mEmitter != null) {
            mEmitter.setDataCallback(null);
        }
        mEmitter = emitter;
        mEmitter.setDataCallback(this);
        mEmitter.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                report(ex);
            }
        });
    }

    @Override
    public int getBytesRead() {
        return totalRead;
    }

    @Override
    public DataTracker getDataTracker() {
        return tracker;
    }

    @Override
    public void setDataTracker(DataTracker tracker) {
        this.tracker = tracker;
    }

    private DataTracker tracker;
    private int totalRead;
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
        if (closed) {
            // this emitter was closed but for some reason data is still being spewed...
            // eat it, nom nom.
            bb.recycle();
            return;
        }
        if (bb != null)
            totalRead += bb.remaining();
        Util.emitAllData(this, bb);
        if (bb != null)
            totalRead -= bb.remaining();
        if (tracker != null && bb != null)
            tracker.onData(totalRead);
        // if there's data after the emitting, and it is paused... the underlying implementation
        // is obligated to cache the byte buffer list.
    }

    @Override
    public boolean isChunked() {
        return mEmitter.isChunked();
    }

    @Override
    public void pause() {
        mEmitter.pause();
    }

    @Override
    public void resume() {
        mEmitter.resume();
    }

    @Override
    public boolean isPaused() {
        return mEmitter.isPaused();
    }

    @Override
    public AsyncServer getServer() {
        return mEmitter.getServer();
    }

    boolean closed;
    @Override
    public void close() {
        closed = true;
        if (mEmitter != null)
            mEmitter.close();
    }

    @Override
    public String charset() {
        if (mEmitter == null)
            return null;
        return mEmitter.charset();
    }
}
