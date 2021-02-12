package com.example.connection.UDP_Connection;

import android.database.Cursor;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Model.User;

import java.io.IOException;
import java.net.DatagramPacket;

public class Multicast_P2P extends Multicast{
    public Multicast_P2P(User user, Database database, ConnectionController connectionController) {
        super(user, database, connectionController);
    }
    @Override
    public void run(){
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
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11], splittedR[12]);//check adduser

                            //Check for the other group owner
                            if(MyNetworkInterface.getMyP2pNetworkInterface("wlan0")!=null){
                                DatagramPacket message = new DatagramPacket(splittedR.toString().getBytes(), splittedR.toString().getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.setNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("wlan0"));
                                multicastSocketGroupwlan0.send(message);

                            }

                        } else {
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11],splittedR[12]);
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
}
