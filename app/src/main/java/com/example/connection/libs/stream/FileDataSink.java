package com.example.connection.libs.stream;

import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.stream.OutputStreamDataSink;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by koush on 2/2/14.
 */
public class FileDataSink extends OutputStreamDataSink {
    File file;
    public FileDataSink(AsyncServer server, File file) {
        super(server);
        this.file = file;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        OutputStream ret = super.getOutputStream();
        if (ret == null) {
            file.getParentFile().mkdirs();
            ret = new FileOutputStream(file);
            setOutputStream(ret);
        }
        return ret;
    }
}
