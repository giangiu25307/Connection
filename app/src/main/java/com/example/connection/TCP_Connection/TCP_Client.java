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
import java.net.Socket;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


public class TCP_Client{
    SSLContext sslContext=null;
    private SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    private SSLSocket sslsocket;
    private Socket clientSocket;
    private   OutputStream out;
    private DataOutputStream dos;
    private String msg="message£€";

    //start a connection with another user --------------------------------------------------------------------------------------------------------------------------------
    public void startConnection(String ip, int port) throws IOException, NoSuchAlgorithmException {
        sslContext = SSLContext.getInstance("TLSv1.2");
        sslsocket=(SSLSocket) sslContext.getSocketFactory().createSocket(ip, port);
        sslsocket.startHandshake();
        out = clientSocket.getOutputStream();
        dos = new DataOutputStream(out);
    }

    //send a message --------------------------------------------------------------------------------------------------------------------------------
    public void sendMessage(String msg) throws IOException {
        this.msg+=msg;
        byte[] array = this.msg.getBytes();
        dos.writeInt(array.length);
        dos.write(array, 0, array.length);
    }

    //send a image throw tcp --------------------------------------------------------------------------------------------------------------------------------
    public void sendImage(ImageView image) throws IOException {
        Bitmap bmp=((BitmapDrawable)image.getDrawable()).getBitmap(); //String str = et.getText().toString();
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
        clientSocket.close();
    }


}