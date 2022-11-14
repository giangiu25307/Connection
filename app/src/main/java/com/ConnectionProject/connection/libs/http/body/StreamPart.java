package com.ConnectionProject.connection.libs.http.body;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.ConnectionProject.connection.libs.DataSink;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.http.NameValuePair;

public abstract class StreamPart extends Part {
    public StreamPart(String name, long length, List<NameValuePair> contentDisposition) {
        super(name, length, contentDisposition);
    }
    
    @Override
    public void write(DataSink sink, CompletedCallback callback) {
        try {
            InputStream is = getInputStream();
            com.ConnectionProject.connection.libs.Util.pump(is, sink, callback);
        }
        catch (Exception e) {
            callback.onCompleted(e);
        }
    }
    
    protected abstract InputStream getInputStream() throws IOException;
}
