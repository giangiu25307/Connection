package com.example.connection.libs.http.filter;

import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.FilteredDataEmitter;
import com.example.connection.libs.http.filter.PrematureDataEndException;

public class ContentLengthFilter extends FilteredDataEmitter {
    public ContentLengthFilter(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    protected void report(Exception e) {
        if (e == null && totalRead != contentLength)
            e = new PrematureDataEndException("End of data reached before content length was read: " + totalRead + "/" + contentLength + " Paused: " + isPaused());
        super.report(e);
    }

    long contentLength;
    long totalRead;
    ByteBufferList transformed = new ByteBufferList();
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
        int remaining = bb.remaining();
        long toRead = Math.min(contentLength - totalRead, remaining);

        bb.get(transformed, (int)toRead);

        int beforeRead = transformed.remaining();

        super.onDataAvailable(emitter, transformed);

        totalRead += (beforeRead - transformed.remaining());
        transformed.get(bb);

        if (totalRead == contentLength)
            report(null);
    }
}
