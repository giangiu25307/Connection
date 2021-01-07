package com.example.connection.localization;


import android.location.Location;

import com.example.connection.Controller.Database;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.UDP_Socket;
import com.example.connection.View.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class LocalizationController {

    UDP_Socket rtt;
    Database database;
    TCP_Client tcp_client;
    GPS gps;
    Connection connection;
    InetAddress ip;


    public LocalizationController(Database database, Connection connection)  throws SocketException {
        rtt = new UDP_Socket();
        this.database = database;
        gps=new GPS(connection);
    }

    public void RTTDistance() throws IOException, NoSuchAlgorithmException {
        if (database.getAccept(database.findId_user(this.ip.toString())).equals("yes")) {//accept only use yes or no
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //fare while fino alla chiusura della finestra
                        rtt.packetSender(LocalizationController.this.ip);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                        tcp_client.startConnection(LocalizationController.this.ip.toString(),50000);
                        tcp_client.sendMessage("RESULT-MEET£€"+database.getMyInformation()[0]+rtt.getRTT()+"£€"+gps.getLatitude()+"£€"+gps.getLongitude(),"");
                }
            }).start();

        }
        else{
            //parte di luca
        }
    }
    public float gpsDirection(double Userlatiutude,double Userlongitude){
        Location User=new Location("user");
        User.setLatitude(Userlatiutude);
        User.setLongitude(Userlongitude);
        return gps.getLocation().bearingTo(User);

    }

    public void acceptMeet() throws IOException, NoSuchAlgorithmException {
        tcp_client.startConnection(ip.toString(),50000);
        tcp_client.sendMessage("REQUEST-MEET£€"+database.getMyInformation()[0],"");
        RTTDistance();
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
}
