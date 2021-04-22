package com.example.connection.libs.parser;

import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.future.Future;
import com.example.connection.libs.parser.AsyncParser;
import com.example.connection.libs.parser.StringParser;

import org.json.JSONArray;

import java.lang.reflect.Type;

/**
 * Created by koush on 5/27/13.
 */
public class JSONArrayParser implements AsyncParser<JSONArray> {
    @Override
    public Future<JSONArray> parse(DataEmitter emitter) {
        return new com.example.connection.libs.parser.StringParser().parse(emitter)
        .thenConvert(JSONArray::new);
    }

    @Override
    public void write(DataSink sink, JSONArray value, CompletedCallback completed) {
        new StringParser().write(sink, value.toString(), completed);
    }

    @Override
    public Type getType() {
        return JSONArray.class;
    }

    @Override
    public String getMime() {
        return "application/json";
    }
}
