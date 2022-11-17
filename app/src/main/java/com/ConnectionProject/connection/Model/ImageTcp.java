package com.ConnectionProject.connection.Model;

import java.util.HashMap;
import java.util.TreeMap;

public class ImageTcp {

    private int length;
    private HashMap<Integer,String> packets;

    public ImageTcp(){
        packets = new HashMap<>();
        length = 0;
    }

    public int length(){
       return packets.size();
    }

    public TreeMap<Integer, String> getPackets() {
        TreeMap<Integer,String> sorted = new TreeMap<>();
        sorted.putAll(packets);
        return sorted;
    }

    public void put(int sequenceNumber, String value){
        packets.put(sequenceNumber, value);
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

}
