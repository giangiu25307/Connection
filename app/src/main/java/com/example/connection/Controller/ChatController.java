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

public class ChatController {

    TCP_Client tcp;
    Multicast udp;
    MultiThreadedServer tcpServer;
    Database database;
    User user;
    public ChatController(Connection connection) {
        tcp = new TCP_Client();
        udp = new Multicast(user,database);
        database=new Database(connection.getApplicationContext());
        String userInfo[]=database.getMyInformation();
        user=new User(userInfo[0],userInfo[1],userInfo[2],userInfo[3],userInfo[4],userInfo[5],userInfo[6],userInfo[7],userInfo[8],userInfo[9],userInfo[10]);
    }

    public void sendGlobalMsg(String msg) {
        udp.sendGlobalMsg(user.getIdUser()+":"+msg);
        database.addGlobalMsg(msg,user.getIdUser());
    }

    public void sendTCPMsg(String msg,String idReceiver) {
        try {
            String ip = database.findIp(idReceiver);
            tcp.startConnection(ip, 50000);
            tcp.sendMessage(msg);
            database.addMsg(msg,idReceiver,user.getIdUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTCPPath(Paths path, String idReceiver) {
        try {
            String ip = database.findIp(idReceiver);
            tcp.startConnection(ip, 50000);
            tcp.sendMessage(path.toString());//encoding to byte DA FARE
            database.addMsg(path.toString(),idReceiver,user.getIdUser());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
