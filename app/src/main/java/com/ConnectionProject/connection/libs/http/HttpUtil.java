package com.ConnectionProject.connection.libs.http;

import com.ConnectionProject.connection.libs.AsyncServer;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.FilteredDataEmitter;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.http.body.AsyncHttpRequestBody;
import com.ConnectionProject.connection.libs.http.body.JSONObjectBody;
import com.ConnectionProject.connection.libs.http.body.MultipartFormDataBody;
import com.ConnectionProject.connection.libs.http.body.StringBody;
import com.ConnectionProject.connection.libs.http.body.UrlEncodedFormBody;
import com.ConnectionProject.connection.libs.http.filter.ChunkedInputFilter;
import com.ConnectionProject.connection.libs.http.filter.ContentLengthFilter;
import com.ConnectionProject.connection.libs.http.filter.GZIPInputFilter;
import com.ConnectionProject.connection.libs.http.filter.InflaterInputFilter;

public class HttpUtil {
    public static AsyncHttpRequestBody getBody(DataEmitter emitter, CompletedCallback reporter, com.ConnectionProject.connection.libs.http.Headers headers) {
        String contentType = headers.get("Content-Type");
        if (contentType != null) {
            String[] values = contentType.split(";");
            for (int i = 0; i < values.length; i++) {
                values[i] = values[i].trim();
            }
            for (String ct: values) {
                if (UrlEncodedFormBody.CONTENT_TYPE.equals(ct)) {
                    return new UrlEncodedFormBody();
                }
                if (JSONObjectBody.CONTENT_TYPE.equals(ct)) {
                    return new JSONObjectBody();
                }
                if (StringBody.CONTENT_TYPE.equals(ct)) {
                    return new StringBody();
                }
                if (ct != null && ct.startsWith(MultipartFormDataBody.PRIMARY_TYPE)) {
                    return new MultipartFormDataBody(contentType);
                }
            }
        }

        return null;
    }
    
    static class EndEmitter extends FilteredDataEmitter {
        private EndEmitter() {
        }
        
        public static EndEmitter create(AsyncServer server, final Exception e) {
            final EndEmitter ret = new EndEmitter();
            // don't need to worry about any race conditions with post and this return value
            // since we are in the server thread.
            server.post(new Runnable() {
                @Override
                public void run() {
                    ret.report(e);
                }
            });
            return ret;
        }
    }
    
    public static DataEmitter getBodyDecoder(DataEmitter emitter, Protocol protocol, com.ConnectionProject.connection.libs.http.Headers headers, boolean server) {
        long _contentLength = -1;
        try {
            String header = headers.get("Content-Length");
            if (header != null)
                _contentLength = Long.parseLong(header);
        }
        catch (NumberFormatException ex) {
        }
        final long contentLength = _contentLength;
        if (-1 != contentLength) {
            if (contentLength < 0) {
                EndEmitter ender = EndEmitter.create(emitter.getServer(), new BodyDecoderException("not using chunked encoding, and no content-length found."));
                ender.setDataEmitter(emitter);
                emitter = ender;
                return emitter;
            }
            if (contentLength == 0) {
                EndEmitter ender = EndEmitter.create(emitter.getServer(), null);
                ender.setDataEmitter(emitter);
                emitter = ender;
                return emitter;
            }
            ContentLengthFilter contentLengthWatcher = new ContentLengthFilter(contentLength);
            contentLengthWatcher.setDataEmitter(emitter);
            emitter = contentLengthWatcher;
        }
        else if ("chunked".equalsIgnoreCase(headers.get("Transfer-Encoding"))) {
            ChunkedInputFilter chunker = new ChunkedInputFilter();
            chunker.setDataEmitter(emitter);
            emitter = chunker;
        }
        else if (server) {
            // if this is the server, and the client has not indicated a request body, the client is done
            EndEmitter ender = EndEmitter.create(emitter.getServer(), null);
            ender.setDataEmitter(emitter);
            emitter = ender;
            return emitter;
        }

        if ("gzip".equals(headers.get("Content-Encoding"))) {
            GZIPInputFilter gunzipper = new GZIPInputFilter();
            gunzipper.setDataEmitter(emitter);
            emitter = gunzipper;
        }        
        else if ("deflate".equals(headers.get("Content-Encoding"))) {
            InflaterInputFilter inflater = new InflaterInputFilter();
            inflater.setDataEmitter(emitter);
            emitter = inflater;
        }

        // conversely, if this is the client (http 1.0), and the server has not indicated a request body, we do not report
        // the close/end event until the server actually closes the connection.
        return emitter;
    }

    public static boolean isKeepAlive(Protocol protocol, com.ConnectionProject.connection.libs.http.Headers headers) {
        // connection is always keep alive as this is an http/1.1 client
        String connection = headers.get("Connection");
        if (connection == null)
            return protocol == Protocol.HTTP_1_1;
        return "keep-alive".equalsIgnoreCase(connection);
    }

    public static boolean isKeepAlive(String protocol, com.ConnectionProject.connection.libs.http.Headers headers) {
        // connection is always keep alive as this is an http/1.1 client
        String connection = headers.get("Connection");
        if (connection == null)
            return Protocol.get(protocol) == Protocol.HTTP_1_1;
        return "keep-alive".equalsIgnoreCase(connection);
    }

    public static long contentLength(Headers headers) {
        String cl = headers.get("Content-Length");
        if (cl == null)
            return -1;
        try {
            return Long.parseLong(cl);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }
}
