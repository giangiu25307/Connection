package com.ConnectionProject.connection.libs.parser;

import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.DataSink;
import com.ConnectionProject.connection.libs.Util;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.callback.DataCallback;
import com.ConnectionProject.connection.libs.future.Future;
import com.ConnectionProject.connection.libs.future.SimpleFuture;

import java.lang.reflect.Type;

/**
 * Created by koush on 5/27/13.
 */
public class ByteBufferListParser implements AsyncParser<ByteBufferList> {
    @Override
    public Future<ByteBufferList> parse(final DataEmitter emitter) {
        final ByteBufferList bb = new ByteBufferList();
        final SimpleFuture<ByteBufferList> ret = new SimpleFuture<ByteBufferList>() {
            @Override
            protected void cancelCleanup() {
                emitter.close();
            }
        };
        emitter.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList data) {
                data.get(bb);
            }
        });

        emitter.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    ret.setComplete(ex);
                    return;
                }

                try {
                    ret.setComplete(bb);
                }
                catch (Exception e) {
                    ret.setComplete(e);
                }
            }
        });

        return ret;
    }

    @Override
    public void write(DataSink sink, ByteBufferList value, CompletedCallback completed) {
        Util.writeAll(sink, value, completed);
    }

    @Override
    public Type getType() {
        return ByteBufferList.class;
    }

    @Override
    public String getMime() {
        return null;
    }
}
