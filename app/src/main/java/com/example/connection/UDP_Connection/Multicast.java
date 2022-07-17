package com.example.connection.UDP_Connection;

import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.PlusController;
import com.example.connection.Database.Database;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.View.Connection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class Multicast extends AsyncTask<Void, Void, Void> implements Runnable {
    protected InetAddress group;
    protected MulticastSocket multicastSocketGroupP2p;
    protected MulticastSocket multicastSocketGroupwlan0;
    protected SocketAddress sa;
    protected TcpClient tcp_client;
    protected ConnectionController connectionController;
    protected Database database;
    public static boolean dbUserEvent;
    protected PlusController plusController;
    protected Connection connection;

    public Multicast(Database database, ConnectionController connectionController, TcpClient tcp_client, Connection connection) {
        this.connectionController = connectionController;
        this.tcp_client = tcp_client;
        this.database = database;
        this.connection = connection;
        dbUserEvent=true;
        try {
            group = InetAddress.getByName("234.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.sa = new InetSocketAddress(group, 6789);
    }

    public Multicast(Database database, PlusController plusController){
        this.plusController = plusController;
        this.database = database;
        dbUserEvent=true;
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
        new Thread(new Multicast(database, connectionController,tcp_client, connection)).start();
        return null;
    }

    protected String arrayToString(String[] array){
        String string="";
        for (int i=0;i<array.length;i++){
            string+=array[i]+"£€";
        }
        return string;
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
