package com.example.connection.Controller;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;

import com.example.connection.Database.Database;
import com.example.connection.Model.RecenteGroupOwner;
import com.example.connection.Model.UserPlus;
import com.example.connection.UDP_Connection.MulticastPlus;
import com.example.connection.UDP_Connection.Multicast_WLAN;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PlusController {

    private ArrayList<RecenteGroupOwner> recentGroupOwner;
    private int spamTimer = 300;
    private int licenseDailyTimer = 86400000;
    private ConnectivityManager connectivityManager;
    private NetworkRequest mobileNetworkRequest, networkRequest;
    private String SSID = "DIRECT-CONNEXION", networkPassword = "12345678";
    private WifiManager wifiManager;
    private int netId;
    private MulticastPlus multicastWLAN;
    private Database database;
    private UserPlus myPlusUser;

    public PlusController(ConnectivityManager connectivityManager, WifiManager wifiManager, Database database) {
        this.database = database;
        this.myPlusUser = database.getMyPlusUser();
        this.wifiManager = wifiManager;
        this.connectivityManager = connectivityManager;
        mobileNetworkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        recentGroupOwner = new ArrayList<>();
        checkRecentGroupOwner();
        checkInternetConnection();
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        this.multicastWLAN = new MulticastPlus(database,this);
    }

    public void checkRecentGroupOwner(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (!recentGroupOwner.isEmpty())
                        if (recentGroupOwner.get(0).getTime() < LocalDateTime.now().getSecond() - spamTimer)
                            recentGroupOwner.remove(0);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void checkInternetConnection() { //TO DO
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    connectivityManager.requestNetwork(mobileNetworkRequest,new ConnectivityManager.NetworkCallback() {
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            super.onAvailable(network);

                        }
                    });
                    try {
                        Thread.sleep(licenseDailyTimer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void addGroupOwner(String idGO) {
        recentGroupOwner.add(new RecenteGroupOwner(idGO, LocalDateTime.now().getSecond()));
    }

    public boolean containsIdGO(String idGO){
        for (int i=0; i<recentGroupOwner.size();i++){
            if(recentGroupOwner.get(i).getId().equals(idGO))return true;
        }
        return false;
    }

    //Connect to a group -----------------------------------------------------------------------------------------------------------------------------------
    public void connectToGroup(final String id) {//GroupOwner groupOwner){//
        wifiConnection(id);
        connectivityManager.requestNetwork(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {

                new CountDownTimer(100, 10) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        if (!wifiManager.getConnectionInfo().getSSID().contains("DIRECT-CONNEXION"))
                            wifiConnection(id);
                        else {
                            multicastWLAN.createMulticastSocketPlus();
                            Thread t1 = new Thread(multicastWLAN);
                            t1.start();
                            multicastWLAN.setUserPlus(myPlusUser);
                            multicastWLAN.sendPromotion();
                        }
                    }
                }.start();
            }
        });
    }

    public void wifiConnection(String id) {
        wifiManager.disconnect();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        String SSID1 = SSID + 0;
        wifiConfig.SSID = String.format("\"%s\"", SSID1);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPassword);
        wifiConfig.priority = 999999999;
        //remember id
        wifiManager.startScan();
        wifiManager.startScan();
        wifiManager.startScan();
        netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(netId, true);
    }

    //Disconnected to a group --------------------------------------------------------------------------------------------------------------------------------
    public void disconnectToGroup() {
        wifiManager.disconnect();
        wifiManager.removeNetwork(netId);
    }

}
