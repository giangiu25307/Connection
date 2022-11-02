package com.example.connection.UDP_Connection;

import android.content.Intent;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.ImageController;
import com.example.connection.Database.Database;
import com.example.connection.Listener.MessageListener;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.View.Connection;
import com.example.connection.View.MapFragment;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class
Multicast_P2P extends Multicast {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public Multicast_P2P(Database database, ConnectionController connectionController, TcpClient tcp_client, Connection connection) {
        super(database, connectionController, tcp_client, connection);
    }

    /**
     * Listening for multicast p2p messages
     */
    @Override
    public void run() {
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        try {
            while (true) {
                multicastSocketGroupP2p.receive(recv);
                String received = new String(recv.getData(), 0, recv.getLength());
                System.out.println("[P2P] "+received);
                String[] splittedR = received.split("£€");//splittedR = splittedResponse if you want change it :P
                boolean iSentIt=false;
                if (splittedR[1].contains("€€")) {
                    if (splittedR[1].split("€€")[1].equals(ConnectionController.myUser.getIdUser())) {
                        iSentIt = true;
                    }
                    splittedR[1] = splittedR[1].split("€€")[0];
                }
                if (!splittedR[1].equals(ConnectionController.myUser.getIdUser())&&!iSentIt) {
                    switch (splittedR[0]) {
                        case "info":
                            //sending my info and receiving the others info -------------------------------------------------------------------------------------------------------------------
                            String groupInfo = "sendInfo£€" + database.getAllMyGroupInfoP2P();
                            splittedR[2] = splittedR[2].split("%")[0] + "%" + MyNetworkInterface.p2pName;
                            database.addUser(splittedR[1], splittedR[2], splittedR[3], splittedR[4], splittedR[5], splittedR[6], splittedR[7], splittedR[8], splittedR[9], splittedR[10], splittedR[11]/*ImageController.decodeImage(splittedR[11], super.connection.getApplicationContext(), splittedR[1])*/, splittedR[12]);
                            tcp_client.sendMessageNoKey(splittedR[2].split("%")[0] + "%" + MyNetworkInterface.p2pName, groupInfo, splittedR[1]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
                                splittedR[2] = database.findIp(ConnectionController.myUser.getIdUser());
                                splittedR[1] += "€€" + ConnectionController.myUser.getIdUser();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent = false;
                            break;
                        case "globalmessage":
                            //receiving a message -----------------------------------------------------------------------------------------------------------------------------------------------

                            database.addGlobalMsg(splittedR[3], splittedR[1]);
                           ;
                            Intent intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "multicast");
                            intent.putExtra("msg", splittedR[3]);
                            intent.putExtra("idUser", splittedR[1]);
                            intent.putExtra("username", splittedR[2]);
                            intent.putExtra("idMessage",  database.getLastGlobalMessageId());
                            intent.putExtra("data", LocalDateTime.now().toString());
                            connection.getApplicationContext().sendBroadcast(intent);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
                                splittedR[2]= database.findIp(ConnectionController.myUser.getIdUser());
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
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
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "GO_LEAVES_BYE":
                            //the group owner is leaving the group -------------------------------------------------------------------------------------------------------------------------------
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
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

                                database.addUser(splittedR[i], splittedR[2].split("%")[0]+"%"+MyNetworkInterface.p2pName,
                                        splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7],
                                        splittedR[i + 8], splittedR[i + 9], ImageController.decodeImage(splittedR[i + 10], super.connection.getApplicationContext(), splittedR[i]),
                                        splittedR[i + 11]);
                            }
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                splittedR[2]= database.findIp(ConnectionController.myUser.getIdUser());
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "firstGroupInfo":
                            String info = "groupInfo£€" + database.getAllMyGroupInfoP2P();
                            for (int i = 1; i < splittedR.length - 1; i = i + 12) {
                                if (i == 1)
                                    database.setMyGroupOwnerIp(splittedR[2].split("%")[0] + "%" + MyNetworkInterface.p2pName, splittedR[i]);
                                database.addUser(splittedR[i], splittedR[i + 1].split("%")[0] + "%" + MyNetworkInterface.p2pName, splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                            }
                            tcp_client.sendMessageNoKey(splittedR[2].split("%")[0] + "%" + MyNetworkInterface.p2pName, info, splittedR[1]);
                            //Check for the other group owner
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                // splittedR[2]= database.findIp(ConnectionController.myUser.getIdUser());
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "userToDelete":
                            database.deleteAllIdUser(splittedR[1]);
                            if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION")) {
                                splittedR[1]+="€€"+ConnectionController.myUser.getIdUser();
                                String string = this.arrayToString(splittedR);
                                DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, group, 6789);
                                multicastSocketGroupwlan0.send(message);
                            }
                            dbUserEvent=false;
                            break;
                        case "promotion":

                            break;
                        default:
                            break;
                    }
                    if(Connection.fragmentName.equals("MAP") && !splittedR[0].equals("globalMessage")) {
                        MapFragment mapFragment = MapFragment.getIstance();
                        mapFragment.graphicRefresh();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create a multicast p2p group on port 6789
     */
    public void createMultigroupP2P() {
        try {
            multicastSocketGroupP2p = new MulticastSocket(port);
            NetworkInterface networkInterface = MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName);
            multicastSocketGroupP2p.setNetworkInterface(networkInterface);
            multicastSocketGroupP2p.joinGroup(sa, networkInterface);
        } catch (UnknownHostException e) {
            System.out.println(e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Send a global message
     * @param msg msg to sent
     */
    public void sendGlobalMsg(String msg) {
        try {
            if(!msg.startsWith("GO_LEAVES_BYE"))
                msg = "globalmessage£€" + ConnectionController.myUser.getIdUser() + "£€" + ConnectionController.myUser.getUsername() + "£€" + msg;
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, port);
            multicastSocketGroupP2p.setTimeToLive(255);
            multicastSocketGroupP2p.send(message);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Close the multicast p2p group
     */
    public void closeMultigroupP2p(){
        multicastSocketGroupP2p.close();
    }

    /**
     * set the multicast wlan
     * @param multicastWlan multicast wlan to set
     */
    public void setMulticastWlan(MulticastSocket multicastWlan){
        multicastSocketGroupwlan0 = multicastWlan;
    }

    /**
     * @return the multicast socket
     */
    public MulticastSocket getMulticastP2P(){
        return multicastSocketGroupP2p;
    }

}
