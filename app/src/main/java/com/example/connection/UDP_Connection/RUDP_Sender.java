package com.example.connection.UDP_Connection;
import com.example.connection.Controller.ConnectionController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.Semaphore;
import java.util.zip.CRC32;

// The following implementation uses the Go-Back-N protocol
public class RUDP_Sender {
    static int data_size = 988;            // (checksum:8, seqNum:4, data<=988) Bytes : 1000 Bytes total
    static int win_size = 10;
    int base;                    // base sequence number of window
    int nextSeqNum;                // next sequence number in window
    String path;                // path of file to be sent
    String fileName;            // filename to be saved by receiver
    Vector<byte[]> packetsList;    // list of generated packets
    boolean isTransferComplete;    // if receiver has completely received the file
    Multicast multicast;
    MulticastSocket multicastSocket;


        public byte[] generatePacket(int seqNum, byte[] dataBytes) {
            byte[] seqNumBytes = ByteBuffer.allocate(4).putInt(seqNum).array();                // Seq num (4 bytes)
            // generate checksum
            CRC32 checksum = new CRC32();
            checksum.update(("image£€"+ ConnectionController.myUser.getIdUser()+"£€").getBytes());
            checksum.update(seqNumBytes);
            checksum.update(dataBytes);
            byte[] checksumBytes = ByteBuffer.allocate(8).putLong(checksum.getValue()).array();    // checksum (8 bytes)

            // generate packet
            ByteBuffer pktBuf =null;
            pktBuf.put(("image£€"+ ConnectionController.myUser.getIdUser()+"£€").getBytes());
            pktBuf.put(checksumBytes);
            pktBuf.put("£€".getBytes());
            pktBuf.put(seqNumBytes);
            pktBuf.put("£€".getBytes());
            pktBuf.put(dataBytes);
            return pktBuf.array();
        }

        // sending process (updates nextSeqNum)
        public void sendImage(String path,String fileName) {
            try {
                // create byte stream
                FileInputStream fis = new FileInputStream(new File(path));

                try {
                    // while there are still packets yet to be received by receiver
                    while (!isTransferComplete) {

                            byte[] out_data = new byte[10];
                            boolean isFinalSeqNum = false;
                                if (nextSeqNum == 0) {
                                    byte[] fileNameBytes = fileName.getBytes();
                                    byte[] fileNameLengthBytes = ByteBuffer.allocate(4).putInt(fileNameBytes.length).array();
                                    byte[] dataBuffer = new byte[data_size];
                                    int dataLength = fis.read(dataBuffer, 0, data_size - 4 - fileNameBytes.length);
                                    byte[] dataBytes = copyOfRange(dataBuffer, 0, dataLength);
                                    ByteBuffer BB = ByteBuffer.allocate(4 + fileNameBytes.length + dataBytes.length);
                                    BB.put(fileNameLengthBytes);    // file name length
                                    BB.put(fileNameBytes);            // file name
                                    BB.put(dataBytes);                // file data slice
                                    out_data = generatePacket(nextSeqNum, BB.array());
                                }
                                // else if subsequent packets
                                else {
                                    byte[] dataBuffer = new byte[data_size];
                                    int dataLength = fis.read(dataBuffer, 0, data_size);
                                    // if no more data to be read, send empty data. i.e. finalSeqNum
                                    if (dataLength == -1) {
                                        isFinalSeqNum = true;
                                        out_data = generatePacket(nextSeqNum, new byte[0]);
                                    }
                                    // else if valid data
                                    else {
                                        byte[] dataBytes = copyOfRange(dataBuffer, 0, dataLength);
                                        out_data = generatePacket(nextSeqNum, dataBytes);
                                    }
                                }
                                packetsList.add(out_data);    // add to packetsList


                            // send the packet
                            multicastSocket.send(new DatagramPacket(out_data, out_data.length,multicast.group , multicast.port));
                            System.out.println("Sender: Sent seqNum " + nextSeqNum);

                            // update nextSeqNum if currently not at FinalSeqNum
                            if (!isFinalSeqNum) nextSeqNum++;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    fis.close();        // close FileInputStream
                    System.out.println("Sender: sk_out closed!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }


    public RUDP_Sender( Multicast multicast,MulticastSocket multicastSocket) {
        base = 0;
        nextSeqNum = 0;
        packetsList = new Vector<byte[]>(win_size);
        data_size+=("image£€"+ ConnectionController.myUser.getIdUser()+"£€").getBytes().length+("£€".getBytes().length)*2;
        isTransferComplete = false;
        this.multicast=multicast;
        this.multicastSocket=multicastSocket;

    }




    // same as Arrays.copyOfRange in 1.6
    public byte[] copyOfRange(byte[] srcArr, int start, int end) {
        int length = (end > srcArr.length) ? srcArr.length - start : end - start;
        byte[] destArr = new byte[length];
        System.arraycopy(srcArr, start, destArr, 0, length);
        return destArr;
    }
}

