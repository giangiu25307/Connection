package com.example.connection.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.CountDownTimer;

import androidx.annotation.RequiresApi;

import com.example.connection.Bluetooth.BluetoothAdvertiser;
import com.example.connection.Bluetooth.BluetoothScanner;
import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;
import com.example.connection.View.WiFiDirectBroadcastReceiver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    Database database;
    int count=0;
    Collection<WifiP2pDevice> peers;
    List<WifiP2pDevice> newList;
    HashMap<String, String> macAdresses;
    InetAddress groupOwnerAddress;
    WifiP2pManager.PeerListListener peerListListener;
    boolean DeviceFound;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    List<String> bluetoothDevices;
    BluetoothScanner bleScanner;
    BluetoothAdvertiser beacon;
    WifiP2pDevice GroupOwner;

    public ConnectionController(Connection connection, Database database,User user) {
        this.connection = connection;
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        ConnectionToDevice = "";
        ConnectionStatus = "";
        this.database = database;
        this.user=user;
        bluetoothDevices=new ArrayList<>();
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        connection.registerReceiver(wifiScanReceiver, intentFilter);
        macAdresses = new HashMap<>();
        peers = new ArrayList<WifiP2pDevice>();
        newList = new ArrayList<WifiP2pDevice>();
        boolean DeviceFound=true;
        GroupOwner=new WifiP2pDevice();
        SearchPeers();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel,peerListListener,connectionInfoListener);
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        //Discovery();
    }

    // this exist for the handshake when two group owner are meeting each other, THIS NEED TO BE FINISH BEFORE THE APP IS RELEASED -----------------------------------------------------------------------------------
    private void checkDevices() {
        if (group.getClientList().size() == 1) {
            try {
                tcpClient.startConnection("192.168.49.1", 50000);

            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
    }

    //Remove a group --------------------------------------------------------------------------------------------------------------------------------
    private void removeGroup() {
        mManager.removeGroup(mChannel, null);
    }

    //Create a group --------------------------------------------------------------------------------------------------------------------------------
    public void createGroup() {
        System.out.println("create group");
        //mManager.removeGroup(mChannel,null);
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener(){

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
                System.out.println(reason);
            }
        });

    }
public void GetDeviceName(){
    mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
        @Override
        public void onGroupInfoAvailable(WifiP2pGroup group) {
           WifiP2pDevice device=group.getOwner();
            System.out.println(device.deviceName);
            // beacon=new BluetoothAdvertiser(group.getNetworkName());
            //beacon.startBLE();
        }
    });
}
    public void getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("p2p0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                 return;
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                System.out.println( res1.toString());
            }
        } catch (Exception ex) {
            //handle exception
        }
        return ;
    }
    //Scan for the near group --------------------------------------------------------------------------------------------------------------------------------
    private void scan() {
        boolean success = wifiManager.startScan();

        if (!success) {
            scanFailure();

        }
        scanSuccess();
    }

    //The scan has found an our wifi p2p --------------------------------------------------------------------------------------------------------------------------------
    private void scanSuccess() {

        results = wifiManager.getScanResults();


    }

    //The scan has not found an our wifi p2p --------------------------------------------------------------------------------------------------------------------------------
    private void scanFailure() {
        // handle failure: new scan did NOT succeed
        // consider using old scan results: these are the OLD results!
        results = wifiManager.getScanResults();
    }

    //Connection to a group---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void connectionToGroup() {
        config=new WifiP2pConfig();
        config.deviceAddress=GroupOwner.deviceAddress;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        System.out.println(info.groupFormed);
                    }
                });
                ConnectionToDevice = "Connected to the group";
            }

            @Override
            public void onFailure(int reason) {
                System.out.println(reason);
               /* if (count < 3) {
                    new CountDownTimer(3000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            count++;
                            connectionToGroup();
                        }
                    }.start();
                }
                else{
                    scan();
                }

                */
            }
        });

    }

    //Disconnected to a group --------------------------------------------------------------------------------------------------------------------------------
    public void disconnectToGroup() {
        udpClient.imLeaving();
        mManager.cancelConnect(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("disconnected");
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("failed to disconnect");
            }
        });
    }

    //set the config to an our wifi p2p --------------------------------------------------------------------------------------------------------------------------------






    //measure the power connection between me and the group owner --------------------------------------------------------------------------------------------------------------------------------
    public void clientList() {
        WifiManager wifiManager = (WifiManager) connection.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        if (level<=2){
            disconnectToGroup();
        }
    }

    //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
    public void GOLeaves(){
        final String maxId = database.getMaxId();
        try {
            tcpClient.startConnection(database.findIp(maxId),50000);
            tcpClient.sendMessage("GO_LEAVES_BY£€");
            new CountDownTimer(3000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                udpClient.sendGlobalMsg("GO_LEAVES_BYE£€"+maxId);
                }
            }.start();
            this.removeGroup();
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    //return the all client list --------------------------------------------------------------------------------------------------------------------------------
    public Optional<Cursor> getAllClientList(){

            return Optional.of(database.getAllUsers());

    }




    public String Discovery() {
        //RICERCA DISPOSITIVI VICINI-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
        //bleScanner =new BluetoothScanner();
        //bleScanner.startBLEScan();
        //bluetoothDevices= bleScanner.getBleDevices();
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                System.out.println("discovery");
                ConnectionStatus = "Discovery Started";
                SearchPeers();
            }

            @Override
            public void onFailure(int reason) {
                System.out.println(reason);
            }
        });
        return ConnectionStatus;
    }

    public boolean SearchPeers() {
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                newList.addAll(peerList.getDeviceList());
                if (!newList.equals(peers)) {
                    peers.clear();
                    peers.addAll(peerList.getDeviceList());
                    for (WifiP2pDevice device : peerList.getDeviceList()) {
                        System.out.println(device.deviceName);
                            if(device.deviceAddress.equals("f2:25:b7:d4:6e:f5")) {
                                GroupOwner=device;
                                connectionToGroup();
                            }
                    }
                }

            }
        };
        return DeviceFound;
    }

    public String ConnectionListener(){
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
    }

}
