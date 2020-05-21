package com.example.connection.localization;


import com.example.connection.Controller.Database;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.RTTSocket;
import com.example.connection.View.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class localizationController {

    RTTSocket rtt;
    Database database;
    TCP_Client tcp_client;
    GPS gps;
    Connection connection;


    public localizationController(Database database,Connection connection) throws SocketException {
        rtt = new RTTSocket();
        this.database = database;
        gps=new GPS(connection);
    }

    public void RTTDistance(InetAddress ip) throws IOException, NoSuchAlgorithmException {
        if (database.getAccept()) {
           rtt.socketSender(ip);
            tcp_client.startConnection(ip.toString(),50000);
            tcp_client.sendMessage("RESULT-MEET£€"+rtt.getRTT()+"£€"+gps.getLatitude()+"£€"+gps.getLongitude());

        }
    }

    public void acceptMeet(InetAddress ip) throws IOException, NoSuchAlgorithmException {
        tcp_client.startConnection(ip.toString(),50000);
        tcp_client.sendMessage("REQUEST-MEET£€"+database.getMyInformation()[0]);
        RTTDistance(ip);
    }

}
