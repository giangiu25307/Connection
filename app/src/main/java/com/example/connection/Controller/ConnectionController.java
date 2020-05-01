package com.example.connection.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.View.Connection;
import com.example.connection.View.WiFiDirectBroadcastReceiver;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConnectionController {

    WifiManager wifiManager;
    WifiP2pConfig config;
    Connection connection;
    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    String ConnectionStatus, ConnectionToDevice;
    List<WifiP2pDevice> peers;
    List<WifiP2pDevice> newList;
    HashMap<String, String> macAdresses;
    TCP_Client client;
    BroadcastReceiver wifiScanReceiver;
    IntentFilter intentFilter;
    List<ScanResult> results;
    /*WifiP2pDevice[] deviceArray;
    InetAddress groupOwnerAddress;
    WifiP2pManager.PeerListListener peerListListener;
    String[] deviceNameArray;
    boolean DeviceFound;
    WifiManager wifiManager;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;*/

    public ConnectionController(Connection connection) {

        this.connection = connection;
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        ConnectionToDevice = "";
        ConnectionStatus = "";
        macAdresses = new HashMap<>();
        peers = new ArrayList<WifiP2pDevice>();
        newList = new ArrayList<WifiP2pDevice>();
        /*boolean DeviceFound=true;
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
        config = new WifiP2pConfig.Builder()
                .setNetworkName("DIRECT-CONNEXION")
                .setPassphrase("12345678")
                .setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_2GHZ)
                .enablePersistentMode(false)
                .build();
        if(!checkWifiScanResult())CreateGroup();
        //else ConnectionToGroup(); Manca la modifica della network del config per l'autoconnessione
    }

    private void CreateGroup() {
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
    }

    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        results = wifiManager.getScanResults();
    }

    private boolean checkWifiScanResult(){
    //CONTROLLA SE NEL RISULTATO DEL SCANSIONE WIFI HA TROVATO UNA RETE CREATA DALLA NOSTRA APP-------------------------------------------------------------------------------------------------------------------
        for (int i=0;i<results.size();i++){
            if (results.get(i).SSID.contains("DIRECT-"))return true;
        }
        return false;
    }

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

    private void ConnectionToGroup() {
        //CONNESSIONE GRUPPO---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        /*final WifiP2pDevice device = deviceArray[i];
        if (!macAdresses.containsValue(device.deviceAddress))
            macAdresses.put(device.deviceName, device.deviceAddress);
        config.deviceAddress = device.deviceAddress;*/
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                ConnectionToDevice = "Connected to the group";
            }

            @Override
            public void onFailure(int reason) {
                ConnectionToDevice = "Not Connected";
            }
        });
    }

    public void SendMessage(String message) {
        //INVIO MESSAGGI-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        String msg = message;
        //if (!config.deviceAddress.equals(groupOwnerAddress.getHostAddress())) {
        client = new TCP_Client();
        try {
            client.startConnection(null/*groupOwnerAddress.getHostAddress()*/, 8080);
            client.sendMessage(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
    }

    private void setConfig(){
        config=null;
    }

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
