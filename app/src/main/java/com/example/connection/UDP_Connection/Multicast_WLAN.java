package com.example.connection.UDP_Connection;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.TCP_Connection.TcpClient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class Multicast_WLAN extends Multicast {

    public Multicast_WLAN(Database database, ConnectionController connectionController, TcpClient tcp_client) {
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
                System.out.println("[ WLAN ] "+received);
                String[] splittedR = received.split("£€");
                boolean iSentIt = false;
                if (splittedR[1].contains("€€")) {
                    if (splittedR[1].split("€€")[1].equals(ConnectionController.myUser.getIdUser())) {
                        iSentIt = true;
                    }
                    splittedR[1] = splittedR[1].split("€€")[0];
                }
                if(!splittedR[1].equals(ConnectionController.myUser.getIdUser())&&!iSentIt) {
                    switch (splittedR[0]) {
                        case "info":
                            database.addUser(splittedR[1], splittedR[2].split("%")[0]+"%"+MyNetworkInterface.wlanName, splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11], splittedR[12]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                splittedR[2]= database.getMyGroupOwnerIp();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "globalMessage":
                            //receiving a message -----------------------------------------------------------------------------------------------------------------------------------------------
                            database.addGlobalMsg(splittedR[3], splittedR[1]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                splittedR[2]= database.getMyGroupOwnerIp();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
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
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "GO_LEAVES_BYE":
                            ConnectionController.GO_leave=true;
                            //the group owner is leaving the group -------------------------------------------------------------------------------------------------------------------------------
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                String newMessage = database.detectAllOtherGroupClient();
                                database.deleteAllIdUser(newMessage);
                                newMessage = "userToDelete£€" + newMessage;
                                DatagramPacket message = new DatagramPacket(newMessage.getBytes(), newMessage.getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                                if(!splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                                    connectionController.connectToGroupOwnerId(splittedR[1]);
                                }
                                ConnectionController.GO_leave=false;
                            } else {
                                database.deleteAllUser();
                                if (ConnectionController.myUser.getIdUser().equals(splittedR[1]))
                                    connectionController.createGroup();
                                else connectionController.connectToGroupOwnerId(splittedR[1]);
                            }
                            dbUserEvent=false;
                            break;
                        case "groupInfo":
                            //I'll add the user with the ip of the GO instead
                            for (int i = 1; i < splittedR.length - 14; i = i + 12) {
                                if (i == 1)
                                    database.setMyGroupOwnerIp(splittedR[2].split("%")[0] + "%" + MyNetworkInterface.wlanName, splittedR[i]);
                                database.addUser(splittedR[i], splittedR[2].split("%")[0]+"%"+MyNetworkInterface.wlanName, splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                                database.setOtherGroup(splittedR[i]);
                            }
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                splittedR[2]= database.getMyGroupOwnerIp();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupP2p.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "userToDelete":
                            database.deleteAllIdUser(splittedR[1]);
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
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
            String info = "firstGroupInfo£€" + database.getAllMyGroupInfo();
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
            multicastSocketGroupwlan0.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMulticastSocketWlan0() {
        try {
            multicastSocketGroupwlan0 = new MulticastSocket(6789);
            NetworkInterface networkInterface = MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName);
            multicastSocketGroupwlan0.setNetworkInterface(networkInterface);
            multicastSocketGroupwlan0.joinGroup(sa, networkInterface);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMulticastP2P(MulticastSocket multicastP2P){
        multicastSocketGroupP2p = multicastP2P;
    }

    public MulticastSocket getMulticastWlan(){
        return multicastSocketGroupwlan0;
    }
}
