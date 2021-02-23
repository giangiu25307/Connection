package com.example.connection.UDP_Connection;

import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.TCP_Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Multicast extends AsyncTask<Void, Void, Void> implements Runnable {
    protected InetAddress group;
    protected MulticastSocket multicastSocketGroupP2p;
    protected MulticastSocket multicastSocketGroupwlan0;
    protected SocketAddress sa;
    protected TCP_Client tcp_client;
    protected ConnectionController connectionController;
    protected Database database;

    public Multicast(Database database, ConnectionController connectionController,TCP_Client tcp_client) {
        this.connectionController = connectionController;
        this.tcp_client = tcp_client;
        this.database = database;
        try {
            group = InetAddress.getByName("234.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.sa = new InetSocketAddress(group, 6789);

    }

    @Override
    public void run() {


    }

    @Override
    protected Void doInBackground(Void... voids) {
        new Thread(new Multicast(database, connectionController,tcp_client)).start();
        return null;
    }

    //MAYBE WE NEED TO ADD MULTICAST LOCK !!!!!!!!!!!!!!!!!!!!!!!!!!!

    /*public void sendGroupMsg(String idGroup, String msg) {
        try {
            msg = idGroup + "£€" + user.getIdUser() + "£€" + user.getUsername() + "£€" + msg; //IMPLEMENTARE CRIPTAZIONEEEEEEEEEE
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            s.setTimeToLive(255);
            s.send(message);
            database.addGlobalMsg(msg, user.getIdUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
}
