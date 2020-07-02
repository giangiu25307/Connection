package com.example.connection.UDP_Connection;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MyNetworkInterface extends java.net.SocketAddress {



public static NetworkInterface getMyP2pNetworkInterface(){
    Enumeration<NetworkInterface> enumeration = null;
    try {
        enumeration = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
        e.printStackTrace();
    }

    NetworkInterface p2p = null;

    while (enumeration.hasMoreElements()){

        p2p = enumeration.nextElement();
        if (p2p.getName().equals("p2p-wlan0-0")) {
            System.out.println(p2p.getDisplayName());
       return p2p;
        }

    }
    return null;
}


}
