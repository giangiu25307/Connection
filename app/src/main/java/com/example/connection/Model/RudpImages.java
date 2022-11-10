package com.example.connection.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class RudpImages {

    private HashMap<Integer, String> pieces;
    private String idUser;
    private boolean lastPacketArrived;

    public RudpImages(String idUser){
        this.idUser = idUser;
        lastPacketArrived = false;
        pieces = new HashMap<Integer, String>();
    }

    public ArrayList<Integer> getLostPieces(){
        Integer keys[] = pieces.keySet().toArray(new Integer[pieces.size()]);
        int previous = 0;
        ArrayList<Integer> lostPieces = new ArrayList<Integer>();
        for(int i = 0; i < keys.length; i++){
            while(previous<keys[i]){
                lostPieces.add(previous);
                previous++;
            }
            if(previous!=keys[i]) previous = keys[i];
        }
        return lostPieces;
    }

    private void setLastPacketArrived(boolean lastPacketArrived){
        this.lastPacketArrived = lastPacketArrived;
    }

    public void setPieces(HashMap<Integer, String> pieces) {
        this.pieces = pieces;
    }

    public String getIdUser() {
        return idUser;
    }

    public boolean isLastPacketArrived() {
        return lastPacketArrived;
    }
}
