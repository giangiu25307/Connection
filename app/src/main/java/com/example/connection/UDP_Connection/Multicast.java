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
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Multicast extends AsyncTask<Void, Void, Void> implements Runnable {
    public static final String GROUP_OWNER_IP="192.168.49.1";
    private InetAddress group;
    private MulticastSocket multicastSocketGroupP2p;
    private MulticastSocket multicastSocketGroupwlan0;
    private Thread runningThread = null;
    private DatagramSocket socket = null;
    private SocketAddress sa;
    User user;
    TCP_Client tcp_client;
    ConnectionController connectionController;
    private Database database;
    private UDP_Socket udp_socket;
    public Multicast(User user, Database database, ConnectionController connectionController) {
        this.connectionController = connectionController;
        try {
            tcp_client = new TCP_Client();
            this.database = database;
            this.user = user;
            group = InetAddress.getByName("234.0.0.0");
            this.sa = new InetSocketAddress(group, 6789);
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

        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        try {
            while (true) {
                multicastSocketGroupP2p.receive(recv);
                String received = new String(recv.getData(), 0, recv.getLength());
                System.out.println(received);
                String[] splittedR = received.split("£€");
                switch (splittedR[0]) {
                    case "info":
                        //sending my info and receiving the others info -------------------------------------------------------------------------------------------------------------------
                        if (user.getInetAddress().equals(GROUP_OWNER_IP)) {
                            tcp_client.startConnection(splittedR[2], 50000);
                            Cursor c = database.getAllUsers();
                            tcp_client.sendMessage(this.cursorToString(c), "");
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11]);//check adduser

                            //Check for the other group owner
                            if(MyNetworkInterface.getMyP2pNetworkInterface("wlan0")!=null){
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.setNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
                                multicastSocketGroupwlan0.send(message);

                            }

                        } else {
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11]);
                        }
                        break;
                    case "message":
                        //receiving a message -----------------------------------------------------------------------------------------------------------------------------------------------
                        for (int i = 3; i < splittedR.length; i++) {
                            received += splittedR[i];
                        }
                        database.addGlobalMsg(received, splittedR[1]);
                /*} else if (database.checkGroupId(splittedR[0])) {
                    database.addGroupMsg(received, Integer.parseInt(splittedR[1]), Integer.parseInt(splittedR[0]));
                */
                        break;
                    case "localization":
                        //implementare la localizzazione
                        break;
                    case "leave":
                        //A user is leaving the group :( ------------------------------------------------------------------------------------------------------------------------------------
                        database.deleteUser(splittedR[1]);
                        break;
                    case "GO_LEAVES_BYE£€":
                        //the group owner is leaving the group -------------------------------------------------------------------------------------------------------------------------------
                        database.deleteAllUser();
                        connectionController.connectToGroupOwnerId(splittedR[1]);
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            multicastSocketGroupP2p.send(message);
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
    private String cursorToString(Cursor c) {
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
            multicastSocketGroupwlan0.joinGroup(sa, MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
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
