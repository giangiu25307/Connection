package com.example.connection.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;

import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;

import java.io.IOException;
import java.util.List;

public class ConnectionController {

    WifiManager wifiManager;
    WifiP2pConfig config;
    Connection connection;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    String ConnectionStatus, ConnectionToDevice;
    WifiP2pGroup group;
    Multicast udpClient;
    TCP_Client tcpClient;
    BroadcastReceiver wifiScanReceiver;
    IntentFilter intentFilter;
    List<ScanResult> results;
    User user;
    MultiThreadedServer tcpServer;

    public ConnectionController(Connection connection) {
        this.connection = connection;
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        ConnectionToDevice = "";
        ConnectionStatus = "";
        /*macAdresses = new HashMap<>();
        peers = new ArrayList<WifiP2pDevice>();
        newList = new ArrayList<WifiP2pDevice>();
        boolean DeviceFound=true;
        SearchPeers();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel,peerListListener,connectionInfoListener);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);*/
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        Scan();
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        connection.registerReceiver(wifiScanReceiver, intentFilter);
        resetConfig();
    }

    private void checkDevices(){
        if(group.getClientList().size()==1){
            try {
                tcpClient.startConnection("192.168.49.1",50000);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeGroup(){
        mManager.removeGroup(mChannel,null);
        resetConfig();
    }

    private void createGroup() {
        mManager.createGroup(mChannel, config, null);
    }

    private void Scan() {
        boolean success = wifiManager.startScan();
        if (!success) {
            scanFailure();
        }
    }

    private void scanSuccess() {
        results = wifiManager.getScanResults();
        if(getWifiDirectName().equals("")) createGroup();
        else {
            setConfig();
            connectionToGroup();
        }
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        results = wifiManager.getScanResults();
    }

    private void connectionToGroup() {
        //CONNESSIONE GRUPPO---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        /*final WifiP2pDevice device = deviceArray[i];
        if (!macAdresses.containsValue(device.deviceAddress))
            macAdresses.put(device.deviceName, device.deviceAddress);
        config.deviceAddress = device.deviceAddress;*/
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConnectionToDevice = "Connected to the group";
                sendUniqueData();
            }

            @Override
            public void onFailure(int reason) {
                ConnectionToDevice = "Not Connected";
            }
        });
    }

    private void sendUniqueData() {
        //INVIO IP E ID-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        String msg = user.getAll();
        udpClient = new Multicast();
        udpClient.sendMsg(msg);
    }

    private void setConfig(){
        String networkName = getWifiDirectName();
        if(!networkName.equals("")) {
            config = new WifiP2pConfig.Builder()
                    .setNetworkName(networkName)
                    .setPassphrase("12345678")
                    .setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_2GHZ)
                    .enablePersistentMode(false)
                    .build();
        }
    }

    private String getWifiDirectName(){
        String networkName="";
        for (int i=0; i<results.size();i++){
            if (results.get(i).SSID.contains("DIRECT-"))networkName=results.get(i).SSID;
        }
        return networkName;
    }

    private void resetConfig(){
        config = new WifiP2pConfig.Builder()
                .setNetworkName("DIRECT-"+user.getIdUser())
                .setPassphrase("12345678")
                .setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_2GHZ)
                .enablePersistentMode(false)
                .build();
    }

    /*Collection<WifiP2pDevice> peers;
    List<WifiP2pDevice> newList;
    HashMap<String, String> macAdresses;
    WifiP2pDevice[] deviceArray;
    InetAddress groupOwnerAddress;
    WifiP2pManager.PeerListListener peerListListener;
    String[] deviceNameArray;
    boolean DeviceFound;
    WifiManager wifiManager;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;*/

    /*public String Discovery() {
        //RICERCA DISPOSITIVI VICINI-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConnectionStatus = "Discovery Started";
                System.out.println("test");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println(reason);
                ConnectionStatus = "Discovery Starting Failed";
            }
        });
        return ConnectionStatus;
    }*/

    /*private boolean SearchPeers() {

        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                WifiP2pDevice groupOwner = new WifiP2pDevice();
                newList.addAll(peerList.getDeviceList());
                System.out.println("ciao");
                /*int i = 0;
                while (i < newList.size()) {
                    if (newList.get(i).isGroupOwner() == true) {
                        groupOwner = newList.get(i);
                    }
                    i = i + 1;
                }
                if (!newList.equals(peers)) {
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());
                    deviceNameArray = new String[peerList.getDeviceList().size()];
                    deviceArray = new WifiP2pDevice[peerList.getDeviceList().size()];
                    int index = 0;
                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        if (peers.get(index).isGroupOwner() == true) {
                            groupOwner = newList.get(index);
                        }
                        deviceNameArray[index] = device.deviceName;
                        deviceArray[index] = device;
                        index++;
                    }
                }
                if (peers.size() == 0) {
                     DeviceFound=false;
                }else{
                    DeviceFound=true;
                }
            }
        };
        return DeviceFound;
    }*/

    /*public String ConnectionListener(){
        //SERVE SOLO A CAPIRE CHI è HOST O CLIENT, DA RIMUOVERE PER CREARE UNA VERA E PROPRIA CHAT-------------------------------------------------------------------------------------------------------------------
        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    ConnectionStatus= "Host";
                } else if (wifiP2pInfo.groupFormed) {
                    ConnectionStatus="Client";
                }
            }
        };
        return ConnectionStatus;
    }

    public BroadcastReceiver getmReceiver() {
        return mReceiver;
    }

    public IntentFilter getmIntentFilter() {
        return mIntentFilter;
    }*/
}
