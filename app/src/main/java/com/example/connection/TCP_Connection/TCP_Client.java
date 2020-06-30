package com.example.connection.TCP_Connection;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.ImageView;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.UDP_Connection.Multicast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


public class TCP_Client {
    private SocketFactory socketfactory;
    private Socket socket;
    //private Socket clientSocket;
    private OutputStream out;
    private DataOutputStream dos;
    private String msg = "message£€";

    //start a connection with another user -----------------------
    public void startConnection(String ip, int port) {
        try {
            socket = socketfactory.createSocket("192.168.49.1", port);
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    //send a message --------------------------------------------------------------------------------------------------------------------------------
    public void sendMessage(String msg) throws IOException {
        this.msg += msg;
        byte[] array = this.msg.getBytes();
        // dos.writeInt(array.length);
        dos.write(array, 0, array.length);
    }

    //send a image throw tcp --------------------------------------------------------------------------------------------------------------------------------
    public void sendImage(ImageView image) throws IOException {
        Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap(); //String str = et.getText().toString();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] array = bos.toByteArray();
        dos.writeInt(array.length);
        dos.write(array, 0, array.length);
        stopConnection();
    }

    //Close the connection --------------------------------------------------------------------------------------------------------------------------------
    private void stopConnection() throws IOException {
        out.close();
        dos.close();
        socket.close();
    }
    /*
public void test()  {
    Socket socket = null;


        //SSL Socket
    SSLContext sslContext = null;
    try {
        sslContext = getSSLContext();
    } catch (Exception e) {
        e.printStackTrace();
    }
    try {
       socket = sslContext.getSocketFactory().createSocket("192.168.1.7", 50000);

    } catch (IOException e) {
        e.printStackTrace();
    }
    ((SSLSocket)socket).setEnabledCipherSuites(((SSLSocket)socket).getSupportedCipherSuites());
    if(socket == null)
    {
        throw new IllegalArgumentException("Socket cannot be null!!!");
    }
    try {
        ((SSLSocket)socket).startHandshake();
        System.out.println("handshake");
    } catch (IOException e) {
        e.printStackTrace();
    }

    try {
        socket.getOutputStream().write(100010);
        socket.getOutputStream().flush();
        socket.getOutputStream().close();

        socket.close();

    } catch (IOException e) {
        e.printStackTrace();
    }

    }
   // String[] array=sslContext.getSocketFactory().getDefaultCipherSuites();


    private SSLContext getSSLContext() throws Exception
    {
        SSLContext sslContext = null;

        TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        } };


        //Create an SSLContext
        sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, trustManager, null);

        return sslContext;
    }*/
}