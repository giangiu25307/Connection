package com.example.connection.Controller;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.connection.Device_Connection.ServiceConnections;
import com.example.connection.Model.GroupOwner;
import com.example.connection.Model.User;
import com.example.connection.Model.WifiConnection;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;
import com.example.connection.View.WiFiDirectBroadcastReceiver;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class
ConnectionController {

    private String SSID, networkPassword;
    private WifiManager wifiManager;
    private Connection connection;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private Multicast udpClient;
    private TCP_Client tcpClient;
    private BroadcastReceiver wifiScanReceiver;
    private IntentFilter intentFilter;
    private User user;
    private Database database;
    private HashMap<String, String> macAdresses;
    private WifiP2pManager.PeerListListener peerListListener;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    private ServiceConnections serviceConnection;

    public static Network mMobileNetwork;

    public ConnectionController(Connection connection, Database database, User user) {
        this.connection = connection;
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        this.database = database;
        this.user = user;
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        connection.registerReceiver(wifiScanReceiver, intentFilter);
        macAdresses = new HashMap<>();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, peerListListener, connectionInfoListener);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        serviceConnection = new ServiceConnections(mManager,mChannel,database);
    }

    //Remove a group --------------------------------------------------------------------------------------------------------------------------------
    private void removeGroup() {
        mManager.removeGroup(mChannel, null);
    }

    //Create a group --------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    public void createGroup() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
                    @Override
                    public void onGroupInfoAvailable(WifiP2pGroup group) {
                        SSID=group.getNetworkName();
                        networkPassword =group.getPassphrase();
                    }
                });

                serviceConnection.registerService(Task.ServiceEntry.serviceGroupOwner,database.getMyInformation()[0],SSID,networkPassword);
                connectToGroup(serviceConnection.findOtherGroupOwner());
                udpClient.createMulticastSocketWlan0();//TO SEE IF IT WORKS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            }
            @Override
            public void onFailure(int reason) {
                System.out.println("ciao"+reason);
            }
        });


    }

    //Connect to a group -----------------------------------------------------------------------------------------------------------------------------------
    public void connectToGroup(GroupOwner groupOwner){
        new WifiConnection(groupOwner.getSSID(),groupOwner.getPassword(),wifiManager);
        serviceConnection.registerService(Task.ServiceEntry.serviceClientConnectedToGroupOwner,database.getMyInformation()[0],groupOwner.getId());
        udpClient.sendInfo();
        if(serviceConnection.clientListeningOtherClient())createGroup();
    }

    //Disconnected to a group --------------------------------------------------------------------------------------------------------------------------------
    public void disconnectToGroup() {
        wifiManager.disconnect();
        udpClient.imLeaving();
    }

    //measure the power connection between me and the group owner --------------------------------------------------------------------------------------------------------------------------------
    public void clientList() {
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        if (level <= 2) {
            disconnectToGroup();
        }
    }

    //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
    public void GOLeaves() {
        final String maxId = database.getMaxId();

        tcpClient.startConnection(database.findIp(maxId), 50000);
        tcpClient.sendMessage("GO_LEAVES_BY£€", "");
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
            }
        }.start();
        this.removeGroup();

    }

    public void broadcastNewGroupOwnerId(){
        udpClient.sendGlobalMsg("GO_LEAVES_BYE£€".concat(database.getMyInformation()[0]));
    }

    //return the all client list --------------------------------------------------------------------------------------------------------------------------------
    public Optional<Cursor> getAllClientList() {

        return Optional.of(database.getAllUsers());

    }

    public BroadcastReceiver getmReceiver() {
        return mReceiver;
    }

    public IntentFilter getmIntentFilter() {
        return mIntentFilter;
    }


    public void initProcess() {

        Optional<GroupOwner> optionalGroupOwner = serviceConnection.lookingForGroupOwner();
        if(optionalGroupOwner.isPresent()){
            connectToGroup(optionalGroupOwner.get());
        } else {
            optionalGroupOwner = serviceConnection.searchAndRequestForIdNetwork();
            if(optionalGroupOwner.isPresent())connectToGroup(optionalGroupOwner.get());
            else createGroup();
        }
    }

    public void active4G(){
        final ConnectivityManager connectivityManager= (ConnectivityManager) connection.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder req = new NetworkRequest.Builder();
        req.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        req.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        NetworkRequest networkRequest=req.build();
        ConnectivityManager.NetworkCallback networkCallback = new
                ConnectivityManager.NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        mMobileNetwork=network;
                        connectivityManager.bindProcessToNetwork(network);
                        connection.startVpn();
                    }
                };
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    //GROUP OWNER IS LEAVING SO I NEED TO CONNECT TO ANOTHER ONE, WHICH ID WAS GIVEN TO ME
    public void connectToGroupOwnerId(String id){
        Optional<GroupOwner> optionalGroupOwner=serviceConnection.lookingForGroupOwner(id);
        if(optionalGroupOwner.isPresent())connectToGroup(optionalGroupOwner.get());
    }

}

/*
    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("p2p0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return null;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            //handle exception
        }
        return null;
    }

    public void MACSender(){
        udpClient.sendGlobalMsg("GO_LEAVES_BYE£€".concat(getMacAddr()));
    }*/