package com.ConnectionProject.connection.libs.parser;

import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.DataSink;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.future.Future;

import org.json.JSONArray;

import java.lang.reflect.Type;

/**
 * Created by koush on 5/27/13.
 */
public class JSONArrayParser implements AsyncParser<JSONArray> {
    @Override
    public Future<JSONArray> parse(DataEmitter emitter) {
        return new com.ConnectionProject.connection.libs.parser.StringParser().parse(emitter)
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
