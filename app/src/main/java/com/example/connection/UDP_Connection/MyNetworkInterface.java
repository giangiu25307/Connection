package com.example.connection.UDP_Connection;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MyNetworkInterface extends java.net.SocketAddress {

    public static String p2pName = "";
    public static String wlanName = "";

    // MyNetworkInterface.p2pName name of the wi-fi direct interface
    public static NetworkInterface getMyP2pNetworkInterface(String nicName) {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        NetworkInterface p2p = null;

        while (enumeration.hasMoreElements()) {

            p2p = enumeration.nextElement();
            if (p2p.getName().equals(nicName)) {
                return p2p;
            }

        }
        return null;
    }

    public static void setNetworkInterfacesNames() throws SocketException {
        Enumeration<NetworkInterface> enumeration = null;
        try {
            enumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
        }

        NetworkInterface interfaces = null;
        int i = 0;
        while (enumeration.hasMoreElements()) {

            interfaces = enumeration.nextElement();
            Enumeration<InetAddress> addresses = interfaces.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress inetAddress = addresses.nextElement();
                if (inetAddress.getHostAddress().equals("192.168.49.1")) {
                    p2pName = interfaces.getName();
                }
                if (inetAddress.getHostAddress().contains("192.168.49") && !inetAddress.getHostAddress().equals("192.168.49.1")) {
                    wlanName = interfaces.getName();
                }
            }
        }
    }

}
