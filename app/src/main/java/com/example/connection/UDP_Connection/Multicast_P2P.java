package com.example.connection.UDP_Connection;

import android.database.Cursor;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;

public class Multicast_P2P extends Multicast {
    public Multicast_P2P(Database database, ConnectionController connectionController) {
        super(database, connectionController);
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
                switch (splittedR[0]) {
                    case "info":
                        //sending my info and receiving the others info -------------------------------------------------------------------------------------------------------------------
                        tcp_client.startConnection(splittedR[2], 50000);
                        tcp_client.sendMessage(database.getAllMyGroupInfo(), splittedR[12]);
                        database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11], splittedR[12]);//check adduser

                        //Check for the other group owner
                        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && connectionController.wifiInfo().contains("DIRECT-CONNEXION")) {
                            DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                            multicastSocketGroupwlan0.send(message);
                        }
                        break;
                    case "message":
                        //receiving a message -----------------------------------------------------------------------------------------------------------------------------------------------
                        for (int i = 3; i < splittedR.length; i++) {
                            received += splittedR[i];
                        }
                        database.addGlobalMsg(received, splittedR[1]);
                        //Check for the other group owner
                        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && connectionController.wifiInfo().contains("DIRECT-CONNEXION")) {
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
                        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && connectionController.wifiInfo().contains("DIRECT-CONNEXION")) {
                            DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                            multicastSocketGroupwlan0.send(message);
                        }
                        break;
                    case "GO_LEAVES_BYE":
                        //the group owner is leaving the group -------------------------------------------------------------------------------------------------------------------------------
                        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && connectionController.wifiInfo().contains("DIRECT-CONNEXION")) {
                            String newMessage = database.detectAllOtherGroupClientByIp(splittedR[2]);
                            database.deleteAllIdUser(newMessage);
                            newMessage = "userToDelete£€"+newMessage;
                            DatagramPacket message = new DatagramPacket(newMessage.getBytes(), newMessage.getBytes().length, group, 6789);
                            multicastSocketGroupwlan0.send(message);
                        }else{
                            database.deleteAllUser();
                        }
                        connectionController.connectToGroupOwnerId(splittedR[1]);
                        break;
                    case "groupInfo":
                        for (int i = 1; i < splittedR.length - 1; i = i + 12) {
                            database.addUser(splittedR[i], splittedR[2], splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                        }
                        //Check for the other group owner
                        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && connectionController.wifiInfo().contains("DIRECT-CONNEXION")) {
                            DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                            multicastSocketGroupwlan0.send(message);
                        }
                        break;
                    case "userToDelete":
                        database.deleteAllIdUser(splittedR[1]);
                        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && connectionController.wifiInfo().contains("DIRECT-CONNEXION")) {
                            DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                            multicastSocketGroupwlan0.send(message);
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
