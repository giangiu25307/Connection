package com.example.connection.Controller;

import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

public class ChatController {

    TCP_Client tcp;
    Multicast udp;
    MultiThreadedServer tcpServer;
    Database database;
    User user;
    ConnectionController connectionController;
    public static ChatController istance = null;

    public static ChatController getInstance() {
        return istance;
    }

    public ChatController newIstance(Connection connection, ConnectionController connectionController) {
        istance = new ChatController();
        setConnectionController(connectionController);
        istance.setDatabase(new Database(connection.getApplicationContext()));
        //String userInfo[]=database.getMyInformation();
        //user=new User(userInfo[0],userInfo[1],userInfo[2],userInfo[3],userInfo[4],userInfo[5],userInfo[6],userInfo[7],userInfo[8],userInfo[9],userInfo[10]);
        istance.setUser(user);
        istance.setTcp(new TCP_Client());
        istance.setUdp(new Multicast(user, database, connectionController));
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

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setUser(User user) {
        this.user = user;
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
        try {
            String ip = database.findIp(idReceiver);
                tcp.startConnection(ip, 50000);
            tcp.sendMessage(msg);
            database.addMsg(msg, user.getIdUser(), idReceiver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //send a direct image -------------------------------------------------------------------------------------------------------------------------------
    public void sendTCPPath(Paths path, String idReceiver) {
        try {
            String ip = database.findIp(idReceiver);
                tcp.startConnection(ip, 50000);
            tcp.sendMessage(path.toString());//encoding to byte DA FARE
            database.addMsg(path, idReceiver, user.getIdUser(), idReceiver);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
