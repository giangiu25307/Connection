package com.example.connection.libs.parser;

import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.future.Future;
import com.example.connection.libs.parser.AsyncParser;
import com.example.connection.libs.parser.StringParser;

import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by koush on 5/27/13.
 */
public class JSONObjectParser implements AsyncParser<JSONObject> {
    @Override
    public Future<JSONObject> parse(DataEmitter emitter) {
        return new com.example.connection.libs.parser.StringParser().parse(emitter).thenConvert(JSONObject::new);
    }

    @Override
    public void write(DataSink sink, JSONObject value, CompletedCallback completed) {
        new StringParser().write(sink, value.toString(), completed);
    }

    @Override
    public Type getType() {
        return JSONObject.class;
    }

    @Override
    public String getMime() {
        return "application/json";
    }
}
