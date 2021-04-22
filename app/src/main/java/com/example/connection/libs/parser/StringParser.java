package com.example.connection.libs.parser;

import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.future.Future;
import com.example.connection.libs.parser.AsyncParser;
import com.example.connection.libs.parser.ByteBufferListParser;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * Created by koush on 5/27/13.
 */
public class StringParser implements AsyncParser<String> {
    Charset forcedCharset;

    public StringParser() {
    }

    public StringParser(Charset charset) {
        this.forcedCharset = charset;
    }

    @Override
    public Future<String> parse(DataEmitter emitter) {
        final String charset = emitter.charset();
        return new ByteBufferListParser().parse(emitter)
        .thenConvert(from -> {
            Charset charsetToUse = forcedCharset;
            if (charsetToUse == null && charset != null)
                charsetToUse = Charset.forName(charset);
            return from.readString(charsetToUse);
        });
    }

    @Override
    public void write(DataSink sink, String value, CompletedCallback completed) {
        new ByteBufferListParser().write(sink, new ByteBufferList(value.getBytes()), completed);
    }

    @Override
    public Type getType() {
        return String.class;
    }

    @Override
    public String getMime() {
        return null;
    }
}
