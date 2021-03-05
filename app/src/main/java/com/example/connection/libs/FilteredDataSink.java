package com.example.connection.libs;

import com.example.connection.libs.BufferedDataSink;
import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataSink;

public class FilteredDataSink extends BufferedDataSink {
    public FilteredDataSink(DataSink sink) {
        super(sink);
        setMaxBuffer(0);
    }
    
    public com.example.connection.libs.ByteBufferList filter(com.example.connection.libs.ByteBufferList bb) {
        return bb;
    }

    @Override
    protected void onDataAccepted(com.example.connection.libs.ByteBufferList bb) {
        ByteBufferList filtered = filter(bb);
        // filtering may return the same byte buffer, so watch for that.
        if (filtered != bb) {
            bb.recycle();
            filtered.get(bb);
        }
    }
}
