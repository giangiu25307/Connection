package com.example.connection.libs.http.body;

import android.text.TextUtils;

import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.DataSink;
import com.example.connection.libs.LineEmitter;
import com.example.connection.libs.LineEmitter.StringCallback;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.ContinuationCallback;
import com.example.connection.libs.callback.DataCallback;
import com.example.connection.libs.future.Continuation;
import com.example.connection.libs.http.AsyncHttpRequest;
import com.example.connection.libs.http.Headers;
import com.example.connection.libs.http.Multimap;
import com.example.connection.libs.http.body.AsyncHttpRequestBody;
import com.example.connection.libs.http.body.FilePart;
import com.example.connection.libs.http.body.Part;
import com.example.connection.libs.http.body.StringPart;
import com.example.connection.libs.http.server.BoundaryEmitter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MultipartFormDataBody extends BoundaryEmitter implements AsyncHttpRequestBody<Multimap> {
    LineEmitter liner;
    Headers formData;
    ByteBufferList lastData;
    com.example.connection.libs.http.body.Part lastPart;

    public interface MultipartCallback {
        public void onPart(com.example.connection.libs.http.body.Part part);
    }

    @Override
    public void parse(DataEmitter emitter, final CompletedCallback completed) {
        setDataEmitter(emitter);
        setEndCallback(completed);
    }

    void handleLast() {
        if (lastData == null)
            return;
        
        if (formData == null)
            formData = new Headers();

        String value = lastData.peekString();
        String name = TextUtils.isEmpty(lastPart.getName()) ? "unnamed" : lastPart.getName();
        com.example.connection.libs.http.body.StringPart part = new com.example.connection.libs.http.body.StringPart(name, value);
        part.mHeaders = lastPart.mHeaders;
        addPart(part);

        formData.add(name, value);

        lastPart = null;
        lastData = null;
    }
    
    public String getField(String name) {
        if (formData == null)
            return null;
        return formData.get(name);
    }
    
    @Override
    protected void onBoundaryEnd() {
        super.onBoundaryEnd();
        handleLast();
    }

    @Override
    protected void onBoundaryStart() {
        final Headers headers = new Headers();
        liner = new LineEmitter();
        liner.setLineCallback(new StringCallback() {
            @Override
            public void onStringAvailable(String s) {
                if (!"\r".equals(s)){
                    headers.addLine(s);
                }
                else {
                    handleLast();
                    
                    liner = null;
                    setDataCallback(null);
                    com.example.connection.libs.http.body.Part part = new com.example.connection.libs.http.body.Part(headers);
                    if (mCallback != null)
                        mCallback.onPart(part);
                    if (getDataCallback() == null) {
//                        if (part.isFile()) {
//                            setDataCallback(new NullDataCallback());
//                            return;
//                        }

                        lastPart = part;
                        lastData = new ByteBufferList();
                        setDataCallback(new DataCallback() {
                            @Override
                            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                                bb.get(lastData);
                            }
                        });
                    }
                }
            }
        });
        setDataCallback(liner);
    }

    public static final String PRIMARY_TYPE = "multipart/";
    public static final String CONTENT_TYPE = PRIMARY_TYPE + "form-data";
    String contentType = CONTENT_TYPE;
    public MultipartFormDataBody(String contentType) {
        Multimap map = Multimap.parseSemicolonDelimited(contentType);
        String boundary = map.getString("boundary");
        if (boundary == null)
            report(new Exception ("No boundary found for multipart/form-data"));
        else
            setBoundary(boundary);
    }

    MultipartCallback mCallback;
    public void setMultipartCallback(MultipartCallback callback) {
        mCallback = callback;
    }
    
    public MultipartCallback getMultipartCallback() {
        return mCallback;
    }

    int written;
    @Override
    public void write(AsyncHttpRequest request, final DataSink sink, final CompletedCallback completed) {
        if (mParts == null)
            return;

        Continuation c = new Continuation(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                completed.onCompleted(ex);
//                if (ex == null)
//                    sink.end();
//                else
//                    sink.close();
            }
        });

        for (final com.example.connection.libs.http.body.Part part: mParts) {
            c.add(new ContinuationCallback() {
                @Override
                public void onContinue(Continuation continuation, CompletedCallback next) throws Exception {
                    byte[] bytes = part.getRawHeaders().toPrefixString(getBoundaryStart()).getBytes();
                    com.example.connection.libs.Util.writeAll(sink, bytes, next);
                    written += bytes.length;
                }
            })
            .add(new ContinuationCallback() {
                @Override
                public void onContinue(Continuation continuation, CompletedCallback next) throws Exception {
                    long partLength = part.length();
                    if (partLength >= 0)
                        written += partLength;
                    part.write(sink, next);
                }
            })
            .add(new ContinuationCallback() {
                @Override
                public void onContinue(Continuation continuation, CompletedCallback next) throws Exception {
                    byte[] bytes = "\r\n".getBytes();
                    com.example.connection.libs.Util.writeAll(sink, bytes, next);
                    written += bytes.length;
                }
            });
        }
        c.add(new ContinuationCallback() {
            @Override
            public void onContinue(Continuation continuation, CompletedCallback next) throws Exception {
                byte[] bytes = (getBoundaryEnd()).getBytes();
                com.example.connection.libs.Util.writeAll(sink, bytes, next);
                written += bytes.length;
            }
        });
        c.start();
    }

    @Override
    public String getContentType() {
        if (getBoundary() == null) {
            setBoundary("----------------------------" + UUID.randomUUID().toString().replace("-", ""));
        }
        return contentType + "; boundary=" + getBoundary();
    }

    @Override
    public boolean readFullyOnRequest() {
        return false;
    }

    int totalToWrite;
    @Override
    public int length() {
        if (getBoundary() == null) {
            setBoundary("----------------------------" + UUID.randomUUID().toString().replace("-", ""));
        }

        int length = 0;
        for (final com.example.connection.libs.http.body.Part part: mParts) {
            String partHeader = part.getRawHeaders().toPrefixString(getBoundaryStart());
            if (part.length() == -1)
                return -1;
            length += part.length() + partHeader.getBytes().length + "\r\n".length();
        }
        length += (getBoundaryEnd()).getBytes().length;
        return totalToWrite = length;
    }
    
    public MultipartFormDataBody() {
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public List<com.example.connection.libs.http.body.Part> getParts() {
        if (mParts == null)
            return null;
        return new ArrayList<>(mParts);
    }

    public void addFilePart(String name, File file) {
        addPart(new FilePart(name, file));
    }
    
    public void addStringPart(String name, String value) {
        addPart(new StringPart(name, value));
    }
    
    private ArrayList<com.example.connection.libs.http.body.Part> mParts;
    public void addPart(com.example.connection.libs.http.body.Part part) {
        if (mParts == null)
            mParts = new ArrayList<com.example.connection.libs.http.body.Part>();
        mParts.add(part);
    }

    @Override
    public Multimap get() {
        return new Multimap(formData.getMultiMap());
    }

    @Override
    public String toString() {
        for (Part part: getParts()) {
            return part.toString();
        }
        return "multipart content is empty";
    }
}
