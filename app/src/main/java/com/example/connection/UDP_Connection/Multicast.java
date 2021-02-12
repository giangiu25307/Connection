package com.example.connection.UDP_Connection;

import android.database.Cursor;
import android.os.AsyncTask;

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

public class Multicast extends AsyncTask<Void, Void, Void> implements Runnable {
    public static final String GROUP_OWNER_IP="192.168.49.1";
    protected InetAddress group;
    protected  MulticastSocket multicastSocketGroupP2p;
    protected MulticastSocket multicastSocketGroupwlan0;
    protected Thread runningThread = null;
    protected DatagramSocket socket = null;
    protected SocketAddress sa;
    User user;
    TCP_Client tcp_client;
    ConnectionController connectionController;
    protected Database database;
    protected UDP_Socket udp_socket;
    public Multicast(User user, Database database, ConnectionController connectionController) {
        this.connectionController = connectionController;

            tcp_client = new TCP_Client();
            this.database = database;
            this.user = user;
        try {
            group = InetAddress.getByName("234.0.0.0");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        this.sa = new InetSocketAddress(group, 6789);

    }
    public void createMultigroupP2P(){
        try {
            multicastSocketGroupP2p = new MulticastSocket(6789);
            multicastSocketGroupP2p.joinGroup(sa, MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0"));

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            udp_socket=new UDP_Socket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {


    }


    //Send a global message ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void sendGlobalMsg(String msg) {
        try {
            msg = "globalmessage£€" + user.getIdUser() + "£€" + user.getUsername() + "£€" + msg;
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupP2p.setNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0"));
            multicastSocketGroupP2p.setTimeToLive(255);
            multicastSocketGroupP2p.send(message);
            // database.addGlobalMsg(msg, user.getIdUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send my info ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void sendInfo() {
        try {
            String info = "info£€" + user.getAll();
            byte[] bytes = info.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupwlan0.send(message);
            System.out.println("letsgo");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //i'm telling everyone that i'm leaving the group ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void imLeaving() {
        try {
            String leave = "leave£€" + user.getIdUser();
            byte[] bytes = leave.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupP2p.send(message);
            database.deleteAllUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        new Thread(new Multicast(user, database, connectionController)).start();
        return null;
    }

    //take all the user group info from the database and transform the in to a BIG string ----------------------------------------------------------------------------------------------------------------
    protected String cursorToString(Cursor c) {
        c.moveToFirst();
        int i = 0;
        String msg = "sendInfo£€";
        while (!c.isAfterLast()) {
            while (i < c.getColumnCount()) {
                msg += c.getString(i) + ",";
                i++;
            }
            msg += ";";
            c.moveToNext();
            i = 0;
        }
        c.close();
        return msg;
    }
    public void createMulticastSocketWlan0(){
        try {
            multicastSocketGroupwlan0 = new MulticastSocket(6789);
            System.out.println(MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
            multicastSocketGroupwlan0.joinGroup(sa,MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
        } catch (IOException e) {
            e.printStackTrace();
        }

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
