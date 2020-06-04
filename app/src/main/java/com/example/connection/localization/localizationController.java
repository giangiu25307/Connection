package com.example.connection.localization;


import android.location.Location;
import android.os.AsyncTask;

import com.example.connection.Controller.Database;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.RTTSocket;
import com.example.connection.View.Connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

public class localizationController  {

    RTTSocket rtt;
    Database database;
    TCP_Client tcp_client;
    GPS gps;
    Connection connection;
    InetAddress ip;


    public localizationController(Database database,Connection connection)  throws SocketException {
        rtt = new RTTSocket();
        this.database = database;
        gps=new GPS(connection);
    }

    public void RTTDistance() throws IOException, NoSuchAlgorithmException {
        if (database.getAccept(database.findId_user(this.ip.toString())).equals("yes")) {//accept only use yes or no
            new Thread(new Runnable() {
                public void run() {
                    try {
                        //fare while fino alla chiusura della finestra
                        rtt.socketSender(localizationController.this.ip);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        tcp_client.startConnection(localizationController.this.ip.toString(),50000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        tcp_client.sendMessage("RESULT-MEET£€"+database.getMyInformation()[0]+rtt.getRTT()+"£€"+gps.getLatitude()+"£€"+gps.getLongitude());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        tcp_client.sendMessage("REQUEST-MEET£€"+database.getMyInformation()[0]);
        RTTDistance();
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }
}