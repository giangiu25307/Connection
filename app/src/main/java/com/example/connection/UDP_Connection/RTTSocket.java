package com.example.connection.UDP_Connection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class RTTSocket extends Thread {

    private DatagramSocket socket;
    private DatagramPacket packet;
    private byte[] buf = new byte[1];
    long startMeasurementTimestamp=0;
    long rtt=0;

    public RTTSocket() throws SocketException {
        socket = new DatagramSocket(4445);
    }

    public void run() {
        packet = new DatagramPacket(buf, buf.length);
        while (true) {

            try {
                socket.receive(packet);
                if(startMeasurementTimestamp!=0) {
                    rtt = System.nanoTime() - startMeasurementTimestamp;
                    calcolateDinstance();

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

        int port = 4445;
        packet = new DatagramPacket(buf, buf.length, ip, port);
        startMeasurementTimestamp = System.nanoTime();
        socket.send(packet);


    }
    public void calcolateDinstance(){
        rtt = rtt * (299792458) / 2;

    }
    public String getRTT(){
        return String.valueOf(rtt);
    }
}
