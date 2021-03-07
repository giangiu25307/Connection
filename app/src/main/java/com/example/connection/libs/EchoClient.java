package com.example.connection.libs;

import com.example.connection.libs.*;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.ConnectCallback;
import com.example.connection.libs.callback.DataCallback;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;

public class EchoClient {

    private String host;
    private int port;

    public EchoClient(String host, int port,String text,String localAddress) {
        AsyncServer.getDefault().setLocalAddress(localAddress);
        this.host = host;
        this.port = port;

    }

    public void setup(String text) {
        System.out.println("Call");
        AsyncServer.getDefault().connectSocket(new InetSocketAddress(host, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                System.out.println("Done");
                handleConnectCompleted(ex, socket,text);
            }
        });
    }

    private void handleConnectCompleted(Exception ex, final AsyncSocket socket,String text) {
        if(ex != null) throw new RuntimeException(ex);

        Util.writeAll(socket, text.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully wrote message");
                socket.close();
            }
        });

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                System.out.println("[Client] Received Message " + new String(bb.getAllByteArray()));
                socket.close();

            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if(ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully end connection");
            }
        });
    }
}