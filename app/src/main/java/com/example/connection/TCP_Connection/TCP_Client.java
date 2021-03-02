package com.example.connection.TCP_Connection;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.View.Connection;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Random;


public class TCP_Client {
    private Socket socket;
    private OutputStream out;
    private DataOutputStream dos;
    private String msg = "message£€",shake="handShake£€";
    private Encryption encryption;
    private Database database;
    private int port1=0;

    public TCP_Client(Database database, Encryption encryption) {
        this.database = database;
        this.encryption = encryption;
        this.socket= new Socket();
    }

    //start a connection with another user -----------------------
    private void startConnection(String ip, int port) {
        try {
            socket.setReuseAddress(true);
            socket = new Socket(ip, port, InetAddress.getByName(database.findIp(ConnectionController.myUser.getIdUser())),40000);
            out = socket.getOutputStream();
            dos = new DataOutputStream(out);
        } catch (IOException e) {
            System.out.println(e);
        }

    }
    private void startConnectionp2p(String ip, int port) {
        try {

            socket = new Socket(ip, port,InetAddress.getByName("192.168.49.1"), port1);
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
            database.createChat(id,database.getUserName(id),secretKey);
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
        Random random = new Random();
        port1=  random.nextInt(20000);
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

            socket.bind(new InetSocketAddress(InetAddress.getByName("192.168.49.1"/*nic.getInterfaceAddresses().get(0).getAddress().getHostAddress()*/), port1));
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkInterface(String id, String ip) {
        try {
            stopConnection();
        } catch (IOException | NullPointerException e ) {
            e.printStackTrace();
        }
        if (database.isOtherGroup(id)) {
            System.out.println("yes");
           // changeNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("wlan0-0"));
            startConnection(ip, 50000);
        } else {
           // changeNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0"));
            startConnectionp2p(ip, 50000);
        }
    }

}