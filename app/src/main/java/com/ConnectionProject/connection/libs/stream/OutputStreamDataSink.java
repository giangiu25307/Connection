package com.ConnectionProject.connection.libs.stream;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import com.ConnectionProject.connection.libs.AsyncServer;
import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataSink;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.callback.WritableCallback;

public class OutputStreamDataSink implements DataSink {
    public OutputStreamDataSink(AsyncServer server) {
        this(server, null);
    }

    @Override
    public void end() {
        try {
            if (mStream != null)
                mStream.close();
            reportClose(null);
        }
        catch (IOException e) {
            reportClose(e);
        }
    }

    AsyncServer server;
    public OutputStreamDataSink(AsyncServer server, OutputStream stream) {
        this.server = server;
        setOutputStream(stream);
    }

    OutputStream mStream;
    public void setOutputStream(OutputStream stream) {
        mStream = stream;
    }
    
    public OutputStream getOutputStream() throws IOException {
        return mStream;
    }

    @Override
    public void write(final ByteBufferList bb) {
        try {
            while (bb.size() > 0) {
                ByteBuffer b = bb.remove();
                getOutputStream().write(b.array(), b.arrayOffset() + b.position(), b.remaining());
                ByteBufferList.reclaim(b);
            }
        }
        catch (IOException e) {
            reportClose(e);
        }
        finally {
            bb.recycle();
        }
    }

    WritableCallback mWritable;
    @Override
    public void setWriteableCallback(WritableCallback handler) {
        mWritable = handler;        
    }

    @Override
    public WritableCallback getWriteableCallback() {
        return mWritable;
    }

    @Override
    public boolean isOpen() {
        return closeReported;
    }

    boolean closeReported;
    Exception closeException;
    public void reportClose(Exception ex) {
        if (closeReported)
            return;
        closeReported = true;
        closeException = ex;

        if (mClosedCallback != null)
            mClosedCallback.onCompleted(closeException);
    }
    
    CompletedCallback mClosedCallback;
    @Override
    public void setClosedCallback(CompletedCallback handler) {
        mClosedCallback = handler;        
    }

    @Override
    public CompletedCallback getClosedCallback() {
        return mClosedCallback;
    }

    @Override
    public AsyncServer getServer() {
        return server;
    }

    WritableCallback outputStreamCallback;
    public void setOutputStreamWritableCallback(WritableCallback outputStreamCallback) {
        this.outputStreamCallback = outputStreamCallback;
    }
}
