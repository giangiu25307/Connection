package com.ConnectionProject.connection.libs.http.server;

import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.FilteredDataEmitter;

import java.nio.ByteBuffer;

public class BoundaryEmitter extends FilteredDataEmitter {
    private byte[] boundary;
    public void setBoundary(String boundary) {
        this.boundary = ("\r\n--" + boundary).getBytes();
    }
    
    public String getBoundary() {
        if (boundary == null)
            return null;
        return new String(boundary, 4, boundary.length - 4);
    }
    
    public String getBoundaryStart() {
        return new String(boundary, 2, boundary.length - 2);
    }
    
    public String getBoundaryEnd() {
        return getBoundaryStart() + "--\r\n";
    }
    
    protected void onBoundaryStart() {
    }
    
    protected void onBoundaryEnd() {
    }
    
    // >= 0 matching
    // -1 matching - (start of boundary end) or \r (boundary start)
    // -2 matching - (end of boundary end)
    // -3 matching \r after boundary
    // -4 matching \n after boundary

    // the state starts out having already matched \r\n

    /*
    Mac:work$ curl -F person=anonymous -F secret=@test.kt  http://localhost:5555

    POST / HTTP/1.1
    Host: localhost:5555
    User-Agent: curl/7.54.0
    Content-Length: 372
    Expect: 100-continue
    Content-Type: multipart/form-data; boundary=------------------------17903558439eb6ff

--------------------------17903558439eb6ff              <--- note! two dashes before boundary
    Content-Disposition: form-data; name="person"

    anonymous
--------------------------17903558439eb6ff              <--- note! two dashes before boundary
    Content-Disposition: form-data; name="secret"; filename="test.kt"
    Content-Type: application/octet-stream

    fun main(args: Array<String>) {
        println("Hello JavaScript!")
    }

--------------------------17903558439eb6ff--            <--- note! two dashes before AND after boundary
            */
    
    
    int state = 2;
    @Override
    public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
//        System.out.println(bb.getString());
//        System.out.println("chunk: " + bb.remaining());
        
//        System.out.println("state: " + state);
        
        // if we were in the middle of a potential match, let's throw that
        // at the beginning of the buffer and process it too.
        if (state > 0) {
            ByteBuffer b = ByteBufferList.obtain(boundary.length);
            b.put(boundary, 0, state);
            b.flip();
            bb.addFirst(b);
            state = 0;
        }
        
        int last = 0;
        byte[] buf = new byte[bb.remaining()];
        bb.get(buf);
        for (int i = 0; i < buf.length; i++) {
            if (state >= 0) {
                if (buf[i] == boundary[state]) {
                    state++;
                    if (state == boundary.length)
                        state = -1;
                }
                else if (state > 0) {
                    // let's try matching again one byte after the start
                    // of last match occurrence
                    i -= state;
                    state = 0;
                }
            }
            else if (state == -1) {
                if (buf[i] == '\r') {
                    state = -4;
                    int len = i - last - boundary.length;
                    if (last != 0 || len != 0) {
                        ByteBuffer b = ByteBufferList.obtain(len).put(buf, last, len);
                        b.flip();
                        ByteBufferList list = new ByteBufferList();
                        list.add(b);
                        super.onDataAvailable(this, list);
                    }
//                    System.out.println("bstart");
                    onBoundaryStart();
                }
                else if (buf[i] == '-') {
                    state = -2;
                }
                else {
                    report(new MimeEncodingException("Invalid multipart/form-data. Expected \r or -"));
                    return;
                }
            }
            else if (state == -2) {
                if (buf[i] == '-') {
                    state = -3;
                }
                else {
                    report(new MimeEncodingException("Invalid multipart/form-data. Expected -"));
                    return;
                }
            }
            else if (state == -3) {
                if (buf[i] == '\r') {
                    state = -4;
                    ByteBuffer b = ByteBufferList.obtain(i - last - boundary.length - 2).put(buf, last, i - last - boundary.length - 2);
                    b.flip();
                    ByteBufferList list = new ByteBufferList();
                    list.add(b);
                    super.onDataAvailable(this, list);
//                    System.out.println("bend");
                    onBoundaryEnd();
                }
                else {
                    report(new MimeEncodingException("Invalid multipart/form-data. Expected \r"));
                    return;
                }
            }
            else if (state == -4) {
                if (buf[i] == '\n') {
                    last = i + 1;
                    state = 0;
                }
                else {
                    report(new MimeEncodingException("Invalid multipart/form-data. Expected \n"));
                }
            }
            else {
                report(new MimeEncodingException("Invalid multipart/form-data. Unknown state?"));
            }
        }

        if (last < buf.length) {
//            System.out.println("amount left at boundary: " + (buf.length - last));
//            System.out.println("State: " + state);
//            System.out.println(state);
            int keep = Math.max(state, 0);
            ByteBuffer b = ByteBufferList.obtain(buf.length - last - keep).put(buf, last, buf.length - last - keep);
            b.flip();
            ByteBufferList list = new ByteBufferList();
            list.add(b);
            super.onDataAvailable(this, list);
        }
    }
}
