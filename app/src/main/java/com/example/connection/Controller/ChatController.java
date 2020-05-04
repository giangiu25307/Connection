package com.example.connection.Controller;

import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ChatController {

    TCP_Client tcp;
    Multicast udp;
    MultiThreadedServer tcpServer;

    public ChatController() {
        tcp = new TCP_Client();
        udp = new Multicast();
    }

    public void sendUDPMsg(String msg) {
        udp.sendMsg(msg);
    }

    public void sendTCPMsg(String msg, String id) {
        try {
            String ip = ipFinder(id);
            tcp.startConnection(ip, 50000);
            tcp.sendMessage(msg);
            tcp.stopConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String ipFinder(String id) {//da basare sul db
        //Luca e Giangiu devono implementare un sistema che quando clicchi la persona desiderata con la quale parlare ritorni l'id
        String ip = "";
        try {
            FileReader fr = new FileReader("data.txt");
            BufferedReader br = new BufferedReader(fr);
            while (br.ready()) {
                String dati = br.readLine();
                if (dati.contains(id)) {
                    String[] data = dati.split(";");
                    ip = data[1];
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
