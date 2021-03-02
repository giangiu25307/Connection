package com.example.connection.UDP_Connection;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.TCP_Connection.TCP_Client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;

public class Multicast_WLAN extends Multicast {
    public Multicast_WLAN(Database database, ConnectionController connectionController, TCP_Client tcp_client) {
        super(database, connectionController, tcp_client);
    }

    @Override
    public void run() {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        try {
            while (true) {
                multicastSocketGroupwlan0.receive(recv);
                String received = new String(recv.getData(), 0, recv.getLength());
                System.out.println(received);
                String[] splittedR = received.split("£€");
                if(!splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                    switch (splittedR[0]) {
                        case "info":

                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11], splittedR[12]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "message":
                            //receiving a message -----------------------------------------------------------------------------------------------------------------------------------------------
                            for (int i = 3; i < splittedR.length; i++) {
                                received += splittedR[i];
                            }
                            database.addGlobalMsg(received, splittedR[1]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
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
                            if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "GO_LEAVES_BYE":
                            //the group owner is leaving the group -------------------------------------------------------------------------------------------------------------------------------
                            if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                String newMessage = database.detectAllOtherGroupClient();
                                database.deleteAllIdUser(newMessage);
                                newMessage = "userToDelete£€" + newMessage;
                                DatagramPacket message = new DatagramPacket(newMessage.getBytes(), newMessage.getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                                connectionController.connectToGroupOwnerId(splittedR[1]);
                            } else {
                                database.deleteAllUser();
                                if (ConnectionController.myUser.getIdUser().equals(splittedR[1]))
                                    connectionController.createGroup();
                                else connectionController.connectToGroupOwnerId(splittedR[1]);
                            }
                            dbUserEvent=false;
                            break;
                        case "groupInfo":
                            //The first user id directly connected to me, so i'll add him with his ip
                            database.setOtherGroup(splittedR[1]);
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11], splittedR[12]);
                            //I'll add the other user with the ip of my GO instead
                            for (int i = 13; i < splittedR.length - 14; i = i + 12) {
                                database.setOtherGroup(splittedR[i]);
                                database.addUser(splittedR[i], "192.168.49.1", splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                            }
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "userToDelete":
                            database.deleteAllIdUser(splittedR[1]);
                            if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send my info ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void sendInfo() {
        try {
            String info = "info£€" + ConnectionController.myUser.getAll();
            System.out.println(info);
            byte[] bytes = info.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupwlan0.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send all group info ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void sendAllMyGroupInfo() {
        try {
            String info = "groupInfo£€" + database.getAllMyGroupInfo();
            byte[] bytes = info.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupwlan0.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //i'm telling everyone that i'm leaving the group ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void imLeaving() {
        try {
            String leave = "leave£€" + ConnectionController.myUser.getIdUser();
            byte[] bytes = leave.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupwlan0.send(message);
            database.deleteAllUser();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMulticastSocketWlan0() {
        try {
            multicastSocketGroupwlan0 = new MulticastSocket(6789);
            multicastSocketGroupwlan0.joinGroup(sa, MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Send a global message ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void sendGlobalMsg(String msg) {
        try {
            msg = "globalmessage£€" + ConnectionController.myUser.getIdUser() + "£€" + ConnectionController.myUser.getUsername() + "£€" + msg;
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, 6789);
            multicastSocketGroupwlan0.setTimeToLive(255);
            multicastSocketGroupwlan0.send(message);
            multicastSocketGroupwlan0.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
