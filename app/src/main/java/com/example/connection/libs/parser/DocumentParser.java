package com.example.connection.libs.parser;

import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.future.Future;
import com.example.connection.libs.http.body.DocumentBody;
import com.example.connection.libs.parser.AsyncParser;
import com.example.connection.libs.parser.ByteBufferListParser;
import com.example.connection.libs.stream.ByteBufferListInputStream;

import org.w3c.dom.Document;

import java.lang.reflect.Type;

import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by koush on 8/3/13.
 */
public class DocumentParser implements AsyncParser<Document> {
    @Override
    public Future<Document> parse(DataEmitter emitter) {
        return new ByteBufferListParser().parse(emitter)
        .thenConvert(from -> DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteBufferListInputStream(from)));
    }

    @Override
    public void write(DataSink sink, Document value, CompletedCallback completed) {
        new DocumentBody(value).write(null, sink, completed);
    }

    @Override
    public Type getType() {
        return Document.class;
    }

    @Override
    public String getMime() {
        return "text/xml";
    }
}
