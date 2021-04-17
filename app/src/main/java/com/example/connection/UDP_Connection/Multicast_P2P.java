package com.example.connection.UDP_Connection;

import android.os.CountDownTimer;
import android.os.Handler;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.TCP_Connection.TcpClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Enumeration;

public class
Multicast_P2P extends Multicast {
    public Multicast_P2P(Database database, ConnectionController connectionController, TcpClient tcp_client) {
        super(database, connectionController, tcp_client);
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
                String[] splittedR = received.split("£€");//splittedR = splittedResponse if you want change it :P
                if (!splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                    switch (splittedR[0]) {
                        case "info":
                            //sending my info and receiving the others info -------------------------------------------------------------------------------------------------------------------
                            String groupInfo= "sendInfo£€"+database.getAllMyGroupInfo();
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11], splittedR[12]);
                            tcp_client.sendMessageNoKey(splittedR[2],groupInfo, splittedR[1]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
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
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
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
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "GO_LEAVES_BYE":
                            //the group owner is leaving the group -------------------------------------------------------------------------------------------------------------------------------
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                String newMessage = database.detectAllOtherGroupClientByIp(splittedR[2]);
                                database.deleteAllIdUser(newMessage);
                                newMessage = "userToDelete£€" + newMessage;
                                DatagramPacket message = new DatagramPacket(newMessage.getBytes(), newMessage.getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            } else {
                                database.deleteAllUser();
                            }
                            connectionController.connectToGroupOwnerId(splittedR[1]);
                            dbUserEvent=false;
                            break;
                        case "groupInfo":
                            for (int i = 1; i < splittedR.length - 1; i = i + 12) {
                                database.addUser(splittedR[i], splittedR[2], splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                            }
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "firstGroupInfo":
                            String info = "groupInfo£€" + database.getAllMyGroupInfo();
                            for (int i = 1; i < splittedR.length - 1; i = i + 12) {
                                database.addUser(splittedR[i], splittedR[2], splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                            }
                            tcp_client.sendMessageNoKey(splittedR[2],info,splittedR[1]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "userToDelete":
                            database.deleteAllIdUser(splittedR[1]);
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
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

    public void createMultigroupP2P() {
        try {
            multicastSocketGroupP2p = new MulticastSocket(6789);
            NetworkInterface networkInterface = MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName);
            multicastSocketGroupP2p.setNetworkInterface(networkInterface);
            multicastSocketGroupP2p.joinGroup(sa,networkInterface);
        } catch (UnknownHostException e) {
            e.printStackTrace();
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
            multicastSocketGroupP2p.setTimeToLive(255);
            multicastSocketGroupP2p.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeMultigroupP2p(){
        multicastSocketGroupP2p.close();
    }
}
