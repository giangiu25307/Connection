package com.example.connection.UDP_Connection;
import com.example.connection.Controller.ConnectionController;
import com.example.connection.View.Connection;

import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
public class RUDP_Receiver {
    static int pkt_size = 1000;
    // Receiver constructor
    public HashMap<Integer,String> receiveImage(String[] data,HashMap<Integer,String> hashMap) {
        pkt_size+=("image£€"+ data[1]+"£€").getBytes().length+("£€".getBytes().length)*2;
                byte[] received_checksum = data[2].getBytes();
                CRC32 checksum = new CRC32();
                checksum.update((data[0] + data[1] + data[3]).getBytes());
                byte[] calculated_checksum = ByteBuffer.allocate(8).putLong(checksum.getValue()).array();

                // if packet is not corrupted
                if (Arrays.equals(received_checksum, calculated_checksum)) {
                    // if final packet (no data), send teardown ack
                    if (data[4].isEmpty()) {
                        return null;

                    }
                    else {
                    hashMap.put(Integer.parseInt(data[2]),data[4]);
                    }
                }
        return hashMap;
    }


    // generate Ack packet
    public byte[] generatePacket(int ackNum){
        byte[] ackNumBytes = ByteBuffer.allocate(4).putInt(ackNum).array();
        // calculate checksum
        CRC32 checksum = new CRC32();
        checksum.update(ackNumBytes);
        // construct Ack packet
        ByteBuffer pktBuf = ByteBuffer.allocate(12);
        pktBuf.put(ByteBuffer.allocate(8).putLong(checksum.getValue()).array());
        pktBuf.put(ackNumBytes);
        return pktBuf.array();
    }

    // same as Arrays.copyOfRange in 1.6
    public byte[] copyOfRange(byte[] srcArr, int start, int end) {
        int length = (end > srcArr.length) ? srcArr.length - start : end - start;
        byte[] destArr = new byte[length];
        System.arraycopy(srcArr, start, destArr, 0, length);
        return destArr;
    }
}
