package com.ConnectionProject.connection.localization;


import android.location.Location;

import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.TCP_Connection.TcpClient;
import com.ConnectionProject.connection.UDP_Connection.UDP_Socket;
import com.ConnectionProject.connection.View.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class LocalizationController {

    UDP_Socket rtt;
    Database database;
    TcpClient tcp_client;
    GPS gps;
    Connection connection;
    InetAddress ip;


    public LocalizationController(Database database, Connection connection)  throws SocketException {
        rtt = new UDP_Socket();
        this.database = database;
        gps=new GPS(connection);
    }

    public float gpsDirection(double Userlatiutude,double Userlongitude){
        Location User=new Location("user");
        User.setLatitude(Userlatiutude);
        User.setLongitude(Userlongitude);
        return gps.getLocation().bearingTo(User);

    }

    public void acceptMeet() throws IOException, NoSuchAlgorithmException {
        tcp_client.sendMessage("REQUEST-MEET£€"+database.getMyInformation()[0],"");
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
}
