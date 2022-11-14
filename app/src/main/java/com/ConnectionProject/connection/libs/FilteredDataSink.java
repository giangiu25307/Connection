package com.ConnectionProject.connection.libs;

public class FilteredDataSink extends BufferedDataSink {
    public FilteredDataSink(DataSink sink) {
        super(sink);
        setMaxBuffer(0);
    }
    
    public com.ConnectionProject.connection.libs.ByteBufferList filter(com.ConnectionProject.connection.libs.ByteBufferList bb) {
        return bb;
    }

    @Override
    protected void onDataAccepted(com.ConnectionProject.connection.libs.ByteBufferList bb) {
        ByteBufferList filtered = filter(bb);
        // filtering may return the same byte buffer, so watch for that.
        if (filtered != bb) {
            bb.recycle();
            filtered.get(bb);
        }
    }
}
