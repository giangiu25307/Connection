package com.example.connection.UDP_Connection;

import com.example.connection.Controller.PlusController;
import com.example.connection.Database.Database;
import com.example.connection.Model.UserPlus;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;

public class MulticastPlus extends Multicast {

    private UserPlus userPlus;

    public MulticastPlus(Database database, PlusController plusController) {
        super(database, plusController);
    }

    @Override
    public void run() {
        super.run();
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                multicastSocketGroupwlan0.receive(recv);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String received = new String(recv.getData(), 0, recv.getLength());
            if(received.split("£€")[1].equals(userPlus.getId()))plusController.disconnectToGroup();
        }
    }

    public void sendPromotion() {
        try {
            String info = "promotion£€" + userPlus.getId() + "£€" /*+ userPlus.getPromotionPage() */+ "£€" + userPlus.getPromotionMessage();
            byte[] bytes = info.getBytes(StandardCharsets.UTF_8);
            DatagramPacket message = new DatagramPacket(bytes, bytes.length, group, port);
            multicastSocketGroupwlan0.send(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMulticastSocketPlus() {
        try {
            multicastSocketGroupwlan0 = new MulticastSocket(port);
            NetworkInterface networkInterface = MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName);
            multicastSocketGroupwlan0.setNetworkInterface(networkInterface);
            multicastSocketGroupwlan0.joinGroup(sa, networkInterface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserPlus(UserPlus userPlus) {
        this.userPlus = userPlus;
    }
}
