package com.example.connection.Controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.connection.Bluetooth.BluetoothAdvertiser;
import com.example.connection.Bluetooth.BluetoothScanner;
import com.example.connection.Database.Database;
import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.Encryption;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.TCP_Connection.TcpServer;
import com.example.connection.UDP_Connection.Multicast_P2P;
import com.example.connection.UDP_Connection.Multicast_WLAN;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.View.Connection;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import static android.net.ConnectivityManager.NetworkCallback;

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
    public static Network mMobileNetwork, mWifiNetwork, mWifiP2pNetwork;
    private Encryption encryption;
    private TcpClient tcpClient;
    private TcpServer tcpServer;
    public static boolean GO_leave = false;
    private WifiManager.WifiLock wifiLock;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public ConnectionController(Connection connection, Database database) {
        this.connection = connection;
        encryption = new Encryption(connection);
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        this.database = database;
        setUser();
        myId = myUser.getIdUser();
        myUser.setPublicKey(encryption.convertPublicKeyToString());
        database.setPublicKey(encryption.convertPublicKeyToString());
        tcpClient = new TcpClient(database, encryption, connection);
        multicastP2P = new Multicast_P2P(database, this, tcpClient);
        multicastWLAN = new Multicast_WLAN(database, this, tcpClient);
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        bluetoothAdvertiser = new BluetoothAdvertiser();
        bluetoothScanner = new BluetoothScanner(connection, this, bluetoothAdvertiser);
        mConfig = new WifiP2pConfig.Builder()
                .setNetworkName(SSID + myId)
                .setPassphrase(networkPassword)
                .setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_AUTO)
                .enablePersistentMode(false)
                .build();
        connManager = (ConnectivityManager) connection.getSystemService(Context.CONNECTIVITY_SERVICE);
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();
        tcpServer = new TcpServer(connection, database, encryption, tcpClient);
        ChatController chatController = new ChatController().newIstance(database, tcpClient, multicastP2P, multicastWLAN, this);
        wifiLock = wifiManager.createWifiLock(1, "testLock");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                disconnectToGroup();
            }
        }));
    }

    //Remove a group --------------------------------------------------------------------------------------------------------------------------------
    public void removeGroup() {
        bluetoothAdvertiser.stopAdvertising();
        wifiLock.release();
        mManager.removeGroup(mChannel, null);
    }

    //Create a group --------------------------------------------------------------------------------------------------------------------------------
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public void createGroup() {
        GO_leave = false;
        mManager.createGroup(mChannel, mConfig, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                bluetoothAdvertiser.stopAdvertising();
                bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceGroupOwner, myId);
                bluetoothAdvertiser.startAdvertising();
                wifiManager.disconnect();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            MyNetworkInterface.setNetworkInterfacesNames();
                            myUser.setInetAddressP2P(MyNetworkInterface.p2pIpv6Address);
                            database.setMyGroupOwnerIp(MyNetworkInterface.p2pIpv6Address, myUser.getIdUser());
                            System.out.println(myUser.getInetAddressP2P().getHostAddress());
                        } catch (SocketException | UnknownHostException e) {
                            e.printStackTrace();
                        }
                        multicastP2P.createMultigroupP2P();
                        if (wifiManager.getConnectionInfo().getSSID().contains("DIRECT-CONNEXION")) {
                            multicastP2P.setMulticastWlan(multicastWLAN.getMulticastWlan());
                            multicastWLAN.setMulticastP2P(multicastP2P.getMulticastP2P());
                        }
                        Thread t1 = new Thread(multicastP2P);
                        t1.start();
                        bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId);
                        tcpServer.setup();
                        tcpServer.setMulticastP2p(multicastP2P);
                    }
                }, 3000);

            }

            @Override
            public void onFailure(int reason) {
                System.out.println("create group error" + reason);

                resetWifi();
                new CountDownTimer(3000, 3000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        createGroup();
                    }
                }.start();


            }
        });
        wifiLock.acquire();
    }

    //Connect to a group -----------------------------------------------------------------------------------------------------------------------------------
    public void connectToGroupWhenGroupOwner(String id) {//GroupOwner groupOwner){//
        tcpServer.close();
        tcpServer = new TcpServer(connection, database, encryption, tcpClient);
        wifiConnection(id);
        connManager.requestNetwork(networkRequest, new NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (!wifiManager.getConnectionInfo().getSSID().contains("DIRECT-CONNEXION"))
                    wifiConnection(id);
                else {
                    new CountDownTimer(5000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            try {
                                MyNetworkInterface.setNetworkInterfacesNames();
                                myUser.setInetAddressWlan(MyNetworkInterface.wlanIpv6Address);
                                database.setIp(myUser.getIdUser(), myUser.getInetAddressWlan().getHostAddress());
                            } catch (SocketException | UnknownHostException e) {
                                e.printStackTrace();
                            }
                            tcpServer.setup();
                            tcpServer.setMulticastP2p(multicastP2P);
                            multicastWLAN.createMulticastSocketWlan0();
                            multicastP2P.setMulticastWlan(multicastWLAN.getMulticastWlan());
                            multicastWLAN.setMulticastP2P(multicastP2P.getMulticastP2P());
                            Thread t1 = new Thread(multicastWLAN);
                            t1.start();
                            multicastWLAN.sendAllMyGroupInfo();
                        }
                    }.start();
                }
            }
        });
    }

    //Connect to a group -----------------------------------------------------------------------------------------------------------------------------------
    public void connectToGroup(final String id) {//GroupOwner groupOwner){//
        wifiConnection(id);
        bluetoothAdvertiser.stopAdvertising();
        bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceClientConnectedToGroupOwner, id);
        bluetoothAdvertiser.stopAdvertising();
        connManager.requestNetwork(networkRequest, new NetworkCallback() {
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
                            tcpServer.setup();
                            try {
                                MyNetworkInterface.setNetworkInterfacesNames();
                                myUser.setInetAddressWlan(MyNetworkInterface.wlanIpv6Address);
                                database.setIp(myUser.getIdUser(), myUser.getInetAddressWlan().getHostAddress());
                            } catch (SocketException | UnknownHostException e) {
                                e.printStackTrace();
                            }
                            multicastWLAN.createMulticastSocketWlan0();
                            Thread t1 = new Thread(multicastWLAN);
                            t1.start();
                            multicastWLAN.sendInfo();
                            bluetoothScanner.initScan(Task.ServiceEntry.serviceClientConnectedToGroupOwner);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (true) {
                                        autoDisconnect();
                                    }
                                }
                            }).start();
                        }
                    }
                }.start();


            }
        });
    }


    //Disconnected to a group --------------------------------------------------------------------------------------------------------------------------------
    public void disconnectToGroup() {
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null) {
            GOLeaves();
        }
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null) {
            multicastWLAN.imLeaving();
        }
        wifiManager.disconnect();
        wifiManager.removeNetwork(netId);
        bluetoothAdvertiser.stopAdvertising();
        tcpServer.close();
    }

    //measure the power connection between me and the group owner --------------------------------------------------------------------------------------------------------------------------------
    public void autoDisconnect() {
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        if (level <= 2 && !GO_leave) {
            disconnectToGroup();
        }
    }

    //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
    private void GOLeaves() {
        multicastP2P.sendGlobalMsg("GO_LEAVES_BYE£€".concat(database.getMaxId()));
        final boolean[] finish = {false};
        int prec = LocalDateTime.now().getSecond();
        while (LocalDateTime.now().getSecond() - prec < 10) ;
        this.removeGroup();
        multicastP2P.closeMultigroupP2p();
    }

    public void initProcess() {
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
                        //connectivityManager.bindProcessToNetwork(network);
                        connection.startVpn();
                    }
                };
        connectivityManager.requestNetwork(networkRequest, networkCallback);
    }

    //GROUP OWNER IS LEAVING SO I NEED TO CONNECT TO ANOTHER ONE, WHICH ID WAS GIVEN TO ME
    public void connectToGroupOwnerId(String id) {
        GO_leave = false;
        bluetoothScanner.setClientToRequestGroupId(id);
        bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId);
    }

    //TESTING DISCONNECTION

    private void setUser() {
        String[] info = database.getMyInformation();
        myUser = new User(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7], info[8], info[9], info[10]);
    }

    public void wifiConnection(String id) {
        wifiManager.disconnect();
        WifiConfiguration wifiConfig = new WifiConfiguration();
        String SSID1 = SSID + 0;
        wifiConfig.SSID = String.format("\"%s\"", SSID1);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPassword);
        wifiConfig.priority = 999999999;
//remember id
        System.out.println(id);
        wifiManager.startScan();
        wifiManager.startScan();
        wifiManager.startScan();
        netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(netId, true);


    }

    public String getSSID() {
        return wifiManager.getConnectionInfo().getSSID();
    }

    public WifiManager getWifiManager() {
        return wifiManager;
    }

    public void resetWifi() {
        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);

    }
}