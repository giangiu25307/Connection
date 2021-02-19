package com.example.connection.Controller;

import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;

import java.nio.file.Paths;

public class ChatController {

    TCP_Client tcp;
    Multicast udp;
    MultiThreadedServer tcpServer;
    Database database;
    ConnectionController connectionController;
    public static ChatController istance = null;

    public static ChatController getInstance() {
        return istance;
    }

    public ChatController newIstance(Database database, ConnectionController connectionController) {
        istance = new ChatController();
        setConnectionController(connectionController);
        setDatabase(database);
        return istance;
    }

    public void setTcp(TCP_Client tcp) {
        this.tcp = tcp;
    }

    public void setUdp(Multicast udp) {
        this.udp = udp;
    }

    public void setTcpServer(MultiThreadedServer tcpServer) {
        this.tcpServer = tcpServer;
    }

    private void setDatabase(Database database) {
        this.database = database;
    }

    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    public ChatController() {
    }


    //Send a global message -------------------------------------------------------------------------------------------------------------------------------
    public void sendGlobalMsg(String msg) {
        udp.sendGlobalMsg(msg);
    }

    //send a direct message -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPMsg(String msg, String idReceiver) {
            String ip = database.findIp(idReceiver);
                tcp.startConnection(ip, 50000);
            tcp.sendMessage(msg,database.getPublicKey(idReceiver));
            database.addMsg(msg, ConnectionController.myUser.getIdUser(), idReceiver);
    }

    //send a direct image -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPPath(Paths path, String idReceiver) {

            String ip = database.findIp(idReceiver);
                tcp.startConnection(ip, 50000);
            tcp.sendMessage(path.toString(),database.getPublicKey(idReceiver));//encoding to byte DA FARE
            database.addMsg(path, idReceiver, ConnectionController.myUser.getIdUser(), idReceiver);

    }
}
