package com.example.connection.UDP_Connection;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

public class Multicast implements Runnable{

    private InetAddress group;
    private MulticastSocket s;
    private Thread runningThread = null;
    private DatagramSocket socket = null;

    public Multicast(){
        try {
            group = InetAddress.getByName("192.168.49.255");
            s.joinGroup(group);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        try (MulticastSocket multicastSocket = s = new MulticastSocket(6789)) {
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // join a Multicast group and send the group salutations
    public void sendMsg(String msg){
        try {
            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length,group, 6789);
            s.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMsg(){
        // get their responses!
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        try {
            s.receive(recv);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String received = new String(recv.getData(), 0, recv.getLength());
        return received;
    }


}
