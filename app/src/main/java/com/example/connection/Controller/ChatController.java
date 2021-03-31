package com.example.connection.Controller;

import com.example.connection.Database.Database;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.UDP_Connection.Multicast_P2P;
import com.example.connection.UDP_Connection.Multicast_WLAN;
import com.example.connection.UDP_Connection.MyNetworkInterface;

import java.nio.file.Paths;

public class ChatController {

    private TcpClient tcp;
    private Multicast_P2P udpP2p;
    private Multicast_WLAN udpWlan;
    private Database database;
    public static ChatController istance = null;
    private ConnectionController connectionController;

    public static ChatController getInstance() {
        return istance;
    }

    public ChatController newIstance(Database database, TcpClient tcp, Multicast_P2P udpP2p, Multicast_WLAN udpWlan, ConnectionController connectionController) {
        istance = new ChatController();
        istance.setDatabase(database);
        istance.setTcp(tcp);
        istance.setUdpP2p(udpP2p);
        istance.setUdpWlan(udpWlan);
        istance.setConnectionController(connectionController);
        return istance;
    }

    public void setTcp(TcpClient tcp) {
        this.tcp = tcp;
    }

    public void setUdpP2p(Multicast_P2P udpP2p) {
        this.udpP2p = udpP2p;
    }

    public void setUdpWlan(Multicast_WLAN udpWlan) {
        this.udpWlan = udpWlan;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    private void setDatabase(Database database) {
        this.database = database;
    }


    public ChatController() {
    }


    //Send a global message -------------------------------------------------------------------------------------------------------------------------------
    public void sendGlobalMsg(String msg) {
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNEXION"))
            udpWlan.sendGlobalMsg(msg);
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null)
            udpP2p.sendGlobalMsg(msg);
    }

    //send a direct message -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPMsg(String msg, String idReceiver) {
        if (database.getSymmetricKey(idReceiver) != null) {

            tcp.sendMessage(msg, idReceiver);
        } else {
            tcp.handShake(idReceiver, database.getPublicKey(idReceiver), msg);
        }

    }

    //send a direct image -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPPath(Paths path, String idReceiver) {
        //DA FARE PER BENE
        /*tcp.sendMessage(path.toString(), idReceiver);//encoding to byte DA FARE
        database.addMsg(path, idReceiver, ConnectionController.myUser.getIdUser(), idReceiver);*/

    }
}
