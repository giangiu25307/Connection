package com.example.connection.TCP_Connection;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;


public class TCP_Client {
    private Socket socket=null;
    private OutputStream out;
    private DataOutputStream dos;
    private String msg = "message£€";
    private Encryption encryption;

    public TCP_Client(Encryption encryption) {
        this.encryption = encryption;
    }
    public TCP_Client() {
    }
    //start a connection with another user -----------------------
    public void startConnection(String ip, int port) {
        try {
            socket=new Socket(ip,port);
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    //send a message --------------------------------------------------------------------------------------------------------------------------------
    public void sendMessage(String msg,String publicKey) {
        this.msg += msg;
        try {
            this.msg=encryption.encrypt(this.msg,encryption.loadPublicKey(publicKey));
        byte[] array = this.msg.getBytes();
        dos.write(array, 0, array.length);
        dos.flush();
        stopConnection();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
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

}