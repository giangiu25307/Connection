package com.example.connection.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.Formatter;

import com.example.connection.Bluetooth.BluetoothAdvertiser;
import com.example.connection.Bluetooth.BluetoothScanner;
import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.Encryption;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast_P2P;
import com.example.connection.UDP_Connection.Multicast_WLAN;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.View.Connection;

import java.net.UnknownHostException;
import java.util.Optional;

import static android.net.ConnectivityManager.*;

public class
ConnectionController {

    private String SSID = "DIRECT-CONNEXION", networkPassword = "12345678";
    private WifiManager wifiManager;
    private Connection connection;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pConfig mConfig;
    private Multicast_P2P multicastP2P;
    private Multicast_WLAN multicastWLAN;
    public static User myUser;
    private Database database;
    private BluetoothScanner bluetoothScanner;
    private BluetoothAdvertiser bluetoothAdvertiser;
    private int netId;
    private String myId;
    private ConnectivityManager connManager;
    private NetworkRequest networkRequest;
    public static Network mMobileNetwork;
    private MultiThreadedServer multiThreadedServer;
    private Encryption encryption;
    private TCP_Client tcp_client;

    public ConnectionController(Connection connection, Database database) {
        this.connection = connection;
        encryption = new Encryption();
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        this.database = database;
        setUser();
        myId = myUser.getIdUser();
        tcp_client = new TCP_Client(database,encryption);
        multicastP2P = new Multicast_P2P(database, this,tcp_client);
        multicastWLAN = new Multicast_WLAN(database, this,tcp_client);
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        bluetoothAdvertiser = new BluetoothAdvertiser();
        bluetoothScanner = new BluetoothScanner(connection,this, bluetoothAdvertiser);
        mConfig = new WifiP2pConfig.Builder()
                .setNetworkName(SSID + myId)
                .setPassphrase(networkPassword)
                .setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_2GHZ)
                .enablePersistentMode(false)
                .build();
        connManager = (ConnectivityManager) connection.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        multiThreadedServer = new MultiThreadedServer(database,connection,this,encryption,tcp_client);
        ChatController chatController = new ChatController().newIstance(database,tcp_client,multicastP2P,multicastWLAN,this);
    }

    //Remove a group --------------------------------------------------------------------------------------------------------------------------------
    public void removeGroup() {
        bluetoothAdvertiser.stopAdvertising();
        mManager.removeGroup(mChannel, null);
    }

    //Create a group --------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("MissingPermission")
    public void createGroup() {
        mManager.createGroup(mChannel, mConfig, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                bluetoothAdvertiser.stopAdvertising();
                bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceGroupOwner, myId);
                bluetoothAdvertiser.startAdvertising();
                wifiManager.disconnect();
                try {
                    myUser.setInetAddress("192.168.49.1");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                multicastP2P.createMultigroupP2P();
                Thread t1 = new Thread(multicastP2P);
                t1.start();
                bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId);
                Handler handler = new Handler();
                encryption.generateAsymmetricKeys();
                database.setPublicKey(encryption.convertPublicKeyToString());handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        multiThreadedServer.openServerSocketP2p();
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                multiThreadedServer.run();
                            }
                        });
                        thread.start();
                    }
                },100);
            }

            @Override
            public void onFailure(int reason) {
                System.out.println("ciao" + reason);
            }
        });


    }

    //Connect to a group -----------------------------------------------------------------------------------------------------------------------------------
    public void connectToGroupWhenGroupOwner(String id) {//GroupOwner groupOwner){//
        wifiConnection(id);
        multicastWLAN.createMulticastSocketWlan0();
        multicastWLAN.sendAllMyGroupInfo();
    }

    //Connect to a group -----------------------------------------------------------------------------------------------------------------------------------
    public void connectToGroup(String id) {//GroupOwner groupOwner){//
        wifiConnection(id);
        bluetoothAdvertiser.stopAdvertising();
        bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceClientConnectedToGroupOwner, id);
        bluetoothAdvertiser.stopAdvertising();
        encryption.generateAsymmetricKeys();
        database.setPublicKey(encryption.convertPublicKeyToString());
        connManager.requestNetwork(networkRequest, new NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                try {
                    String ip = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
                    System.out.println(ip);
                    myUser.setInetAddress(ip);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                multicastWLAN.createMulticastSocketWlan0();
                multicastWLAN.sendInfo();
                bluetoothScanner.initScan(Task.ServiceEntry.serviceClientConnectedToGroupOwner);
                multiThreadedServer.openServerSocketWlan();
                multiThreadedServer.run();
            }
        });
    }


    //Disconnected to a group --------------------------------------------------------------------------------------------------------------------------------
    public void disconnectToGroup() {
        if (MyNetworkInterface.getMyP2pNetworkInterface("p2p-wlan0-0") == null) {
            bluetoothAdvertiser.stopAdvertising();
        }
        multicastWLAN.imLeaving();
        wifiManager.disconnect();
        wifiManager.removeNetwork(netId);
    }

    //measure the power connection between me and the group owner --------------------------------------------------------------------------------------------------------------------------------
    public void autoDisconnect() {
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        if (level <= 2) {
            disconnectToGroup();
        }
    }

    //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
    public void GOLeaves() {
        multicastP2P.sendGlobalMsg("GO_LEAVES_BYE£€".concat(database.getMaxId()));
        if (MyNetworkInterface.getMyP2pNetworkInterface("wlan0") != null && getSSID().contains("DIRECT-CONNEXION")) {
            multicastWLAN.sendGlobalMsg("GO_LEAVES_BYE£€".concat(database.getMaxId()+"£€"+myUser.getInetAddress()));
            wifiManager.disconnect();
            wifiManager.removeNetwork(netId);
        }
        final boolean[] finish = {false};
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finish[0] =true;
            }
        }.start();
        while(finish[0])this.removeGroup();
    }

    //return the all client list --------------------------------------------------------------------------------------------------------------------------------
    public Optional<Cursor> getAllClientList() {
        return Optional.of(database.getAllUsers());
    }

    public void initProcess() {
        setUser();
        bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceLookingForGroupOwner, null);
        bluetoothAdvertiser.startAdvertising();
        bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwner);
    }

    public void active4G() {
        final ConnectivityManager connectivityManager = (ConnectivityManager) connection.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest.Builder req = new NetworkRequest.Builder();
        req.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);
        req.addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        NetworkRequest networkRequest = req.build();
        NetworkCallback networkCallback = new
                NetworkCallback() {

                    @Override
                    public void onAvailable(Network network) {
                        mMobileNetwork = network;
                        connectivityManager.bindProcessToNetwork(network);
                        connection.startVpn();
                    }
                };
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    //GROUP OWNER IS LEAVING SO I NEED TO CONNECT TO ANOTHER ONE, WHICH ID WAS GIVEN TO ME
    public void connectToGroupOwnerId(String id) {
        bluetoothScanner.setClientToRequestGroupId(id);
        bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId);
    }

    //TESTING DISCONNECTION

    private void setUser(){
        String[] info = database.getMyInformation();
        myUser = new User(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7], info[8], info[9], info[10]);
    }

    public void wifiConnection(String id) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", SSID + id);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPassword);
//remember id
        netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    public String getSSID() {
        return wifiManager.getConnectionInfo().getSSID();
    }

}