package com.example.connection.TCP_Connection;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.connection.Controller.Database;
import com.example.connection.UDP_Connection.MyNetworkInterface;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;


public class TCP_Client {
    private Socket socket = null;
    private OutputStream out;
    private DataOutputStream dos;
    private String msg = "message£€",shake="handShake£€";
    private Encryption encryption;
    private Database database;

    public TCP_Client(Database database, Encryption encryption) {
        this.database = database;
        this.encryption = encryption;
    }

    public TCP_Client() {
    }

    //start a connection with another user -----------------------
    private void startConnection(String ip, int port) {
        try {
            socket = new Socket(ip, port);
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
        } catch (IOException e) {
            System.out.println(e);
        }

    }

    public void handShake(String id, String publicKey, String msg) {
        checkInterface(id,database.findIp(id));
        encryption.generateAES();
        shake += id+"£€";
        try {
            String secretKey = encryption.convertSecretKeyToString(encryption.getSecretKey());
            database.setSymmetricKey(secretKey);
            shake += encryption.encrypt(secretKey+"£€"+msg,encryption.convertStringToPublicKey(publicKey));
            byte[] array = shake.getBytes();
            dos.write(array,0, array.length);
            dos.flush();
            stopConnection();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send a message --------------------------------------------------------------------------------------------------------------------------------
    public void sendMessage(String msg, String id) {
        checkInterface(id,database.findIp(id));
        this.msg += id + "£€";
        try {
            byte[] key = encryption.encryptAES(msg, encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
            byte[] array = new byte[this.msg.getBytes().length + key.length];
            System.arraycopy(this.msg.getBytes(), 0, array, 0, this.msg.getBytes().length);
            System.arraycopy(key, 0, array, this.msg.getBytes().length, key.length);
            dos.write(array, 0, array.length);
            dos.flush();
            stopConnection();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessageNoKey(String msg, String id) {
        checkInterface(id,database.findIp(id));
        try {
            byte[] array = msg.getBytes();
            dos.write(array, 0, array.length);
            dos.flush();
            stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send a image throw tcp --------------------------------------------------------------------------------------------------------------------------------//DA CRYPTARE
    public void sendImage(ImageView image, String id) throws IOException {
        Bitmap bmp = ((BitmapDrawable) image.getDrawable()).getBitmap(); //String str = et.getText().toString();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
        byte[] array = new byte[("image£€" + id + "£€").getBytes().length + bos.toByteArray().length];
        System.arraycopy(("image£€" + id + "£€").getBytes(), 0, array, 0, ("image£€" + id + "£€").getBytes().length);
        System.arraycopy(bos.toByteArray(), 0, array, ("image£€" + id + "£€").getBytes().length, bos.toByteArray().length);
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

    private void changeNetworkInterface(NetworkInterface nic) {
        try {
            socket.bind(new InetSocketAddress(nic.getInterfaceAddresses().get(0).getAddress(), 0));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkInterface(String id, String ip) {
        try {
            stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (database.isOtherGroup(id)) {
            changeNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
            startConnection(ip, 50000);
        } else {
            changeNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0"));
            startConnection(ip, 50000);
        }
    }

}