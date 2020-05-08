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
    User user,user1;
    public ChatController(Connection connection) {
        tcp = new TCP_Client();
        udp = new Multicast();
        database=new Database(connection.getApplicationContext());
        user=new User(1,"rikyfaso","123456","rikyfaso999@hotmail.com","maschio","riccardo","fasolo","italia","bologna",3,20);
    }

    public void sendUDPMsg(String msg) {
        udp.sendMsg(String.valueOf(user.getIdUser())+":"+msg);
        database.addGlobalMsg(msg,user.getIdUser());
    }

    public void sendTCPMsg(String msg,String idReceiver) {
        try {
            String ip = database.findIp(idReceiver);
            tcp.startConnection(ip, 50000);
            tcp.sendMessage(msg);
            database.addMsg(msg,Integer.parseInt(idReceiver),user.getIdUser());
            tcp.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendTCPPath(Paths path, String idReceiver) {
        try {
            String ip = database.findIp(idReceiver);
            tcp.startConnection(ip, 50000);
            tcp.sendMessage(path.toString());//encoding to byte DA FARE
            database.addMsg(path.toString(),Integer.parseInt(idReceiver),user.getIdUser());
            tcp.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
