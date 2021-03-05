package com.example.connection.libs;

import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitterBase;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.DataCallback;
import com.example.connection.libs.util.StreamUtility;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by koush on 5/22/13.
 */
public class FileDataEmitter extends DataEmitterBase {
    com.example.connection.libs.AsyncServer server;
    File file;
    public FileDataEmitter(com.example.connection.libs.AsyncServer server, File file) {
        this.server = server;
        this.file = file;
        paused = !server.isAffinityThread();
        if (!paused)
            doResume();
    }

    DataCallback callback;
    @Override
    public void setDataCallback(DataCallback callback) {
        this.callback = callback;
    }

    @Override
    public DataCallback getDataCallback() {
        return callback;
    }

    @Override
    public boolean isChunked() {
        return false;
    }

    boolean paused;
    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
        doResume();
    }

    @Override
    protected void report(Exception e) {
        StreamUtility.closeQuietly(channel);
        super.report(e);
    }

    com.example.connection.libs.ByteBufferList pending = new com.example.connection.libs.ByteBufferList();
    FileChannel channel;
    Runnable pumper = new Runnable() {
        @Override
        public void run() {
            try {
                if (channel == null)
                    channel = new FileInputStream(file).getChannel();
                if (!pending.isEmpty()) {
                    Util.emitAllData(FileDataEmitter.this, pending);
                    if (!pending.isEmpty())
                        return;
                }
                ByteBuffer b;
                do {
                    b = ByteBufferList.obtain(8192);
                    if (-1 == channel.read(b)) {
                        report(null);
                        return;
                    }
                    b.flip();
                    pending.add(b);
                    Util.emitAllData(FileDataEmitter.this, pending);
                }
                while (pending.remaining() == 0 && !isPaused());
            }
            catch (Exception e) {
                report(e);
            }
        }
    };

    private void doResume() {
        server.post(pumper);
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public AsyncServer getServer() {
        return server;
    }

    @Override
    public void close() {
        try {
            channel.close();
        }
        catch (Exception e) {
        }
    }
}
