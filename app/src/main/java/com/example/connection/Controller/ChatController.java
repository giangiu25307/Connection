package com.example.connection.Controller;

import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;

import java.nio.file.Paths;

public class ChatController {

    private TCP_Client tcp;
    private Multicast udp;
    private Database database;
    public static ChatController istance = null;

    public static ChatController getInstance() {
        return istance;
    }

    public ChatController newIstance(Database database, TCP_Client tcp) {
        istance = new ChatController();
        istance.setDatabase(database);
        istance.setTcp(tcp);
        return istance;
    }

    public void setTcp(TCP_Client tcp) {
        this.tcp = tcp;
    }

    public void setUdp(Multicast udp) {
        this.udp = udp;
    }

    private void setDatabase(Database database) {
        this.database = database;
    }


    public ChatController() {
    }


    //Send a global message -------------------------------------------------------------------------------------------------------------------------------
    public void sendGlobalMsg(String msg) {
        udp.sendGlobalMsg(msg);
    }

    //send a direct message -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPMsg(String msg, String idReceiver) {
        if (database.getSymmetricKey(idReceiver) != null) {
            tcp.sendMessage(msg, idReceiver);
        } else {
            tcp.handShake(idReceiver, database.getPublicKey(idReceiver), msg);
        }
        database.addMsg(msg, ConnectionController.myUser.getIdUser(), idReceiver);
    }

    //send a direct image -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPPath(Paths path, String idReceiver) {
        //DA FARE PER BENE
        /*tcp.sendMessage(path.toString(), idReceiver);//encoding to byte DA FARE
        database.addMsg(path, idReceiver, ConnectionController.myUser.getIdUser(), idReceiver);*/

    }
}
