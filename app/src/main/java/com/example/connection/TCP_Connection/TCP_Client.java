package com.example.connection.TCP_Connection;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Paths;


public class TCP_Client {
    private Socket clientSocket;
    private   OutputStream out;
    private DataOutputStream dos;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = clientSocket.getOutputStream();
        dos = new DataOutputStream(out);
    }

    public void sendMessage(String msg) throws IOException {
        byte[] array = msg.getBytes();
        dos.writeInt(array.length);
        dos.write(array, 0, array.length);
    }
    public void sendImage(ImageView image) throws IOException {
        Bitmap bmp=((BitmapDrawable)image.getDrawable()).getBitmap(); //String str = et.getText().toString();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] array = bos.toByteArray();
        dos.writeInt(array.length);
        dos.write(array, 0, array.length);
        stopConnection();
    }


    private void stopConnection() throws IOException {
        out.close();
        dos.close();
        clientSocket.close();
    }
}