package com.example.connection.UDP_Connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class RTTSocket extends Thread {

    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] buf = new byte[1];
    long startMeasurementTimestamp=0;
    long rtt=0;

    public RTTSocket() throws SocketException {
        try {
            socket = new DatagramSocket(50000,InetAddress.getByName("192.168.49.200"));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }


    }
public void test(){
    packet = new DatagramPacket(buf, buf.length);
   try {
        socketSender(InetAddress.getByName("192.168.49.1"));
    } catch (IOException e) {
        e.printStackTrace();
    }
    while (true) {

        try {
            socket.receive(packet);
            System.out.println(packet.getData());
            if(startMeasurementTimestamp!=0) {
                rtt = System.nanoTime() - startMeasurementTimestamp;
                calcolateDinstance();
                System.out.println(getRTT());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (startMeasurementTimestamp == 0) {
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            try {

                socket.send(packet);
                socket.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
        startMeasurementTimestamp=0;
    }
}
    public void run() {
        packet = new DatagramPacket(buf, buf.length);
        /*try {
            socketSender(InetAddress.getByName("192.168.49.1"));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        while (true) {

            try {
                socket.receive(packet);
                if(startMeasurementTimestamp!=0) {
                    rtt = System.nanoTime() - startMeasurementTimestamp;
                    calcolateDinstance();
                   // System.out.println(getRTT());

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (startMeasurementTimestamp == 0) {
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            try {

                    socket.send(packet);
                    socket.close();
                }
            catch(IOException e){
                e.printStackTrace();
            }
                }
            startMeasurementTimestamp=0;
            }
        }

    public void socketSender(InetAddress ip) throws IOException {
        int port = 50000;
        packet = new DatagramPacket(buf, buf.length, ip, port);
        startMeasurementTimestamp = System.nanoTime();
        socket.send(packet);


    }
    public void calcolateDinstance(){
        System.out.println(rtt);
        rtt = rtt * (299792458) / 2;

    }
    public String getRTT(){
        return String.valueOf(rtt);
    }
}
