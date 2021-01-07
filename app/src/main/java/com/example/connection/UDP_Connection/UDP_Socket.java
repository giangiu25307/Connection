package com.example.connection.UDP_Connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP_Socket  {

    private DatagramSocket socket;


    public UDP_Socket() throws SocketException {

            socket = new DatagramSocket();
    }

    public void changeNetworkInterface(NetworkInterface nic ){
        try {
            socket.bind(new InetSocketAddress(nic.getInterfaceAddresses().get(0).getAddress(), 0));
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    public InetAddress getInetSoketAddress(){
        return this.socket.getInetAddress();
    }

   /* public void run() {
        packet = new DatagramPacket(buf, buf.length);

        while (true) {

            try {
                socket.receive(packet);


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }*/

    //method for the udp packet sender
    public void packetSender(InetAddress ip, String msg) throws IOException {
        int port = 50000;
        DatagramPacket packet = new DatagramPacket(msg.getBytes(),msg.getBytes().length, ip, port);
        socket.send(packet);


    }

}
