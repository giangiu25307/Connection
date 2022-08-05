package com.example.connection.Controller;

import static android.net.ConnectivityManager.NetworkCallback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.example.connection.Bluetooth.BluetoothAdvertiser;
import com.example.connection.Bluetooth.BluetoothScanner;
import com.example.connection.Database.Database;
import com.example.connection.Listener.MessageListener;
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

public class
ConnectionController {

    private String SSID = "DIRECT-CONNECTION", networkPassword = "6i4knmjhui524j";
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
    private Encryption encryption;
    private TcpClient tcpClient;
    private TcpServer tcpServer;
    public static boolean GO_leave = false, amIGO;
    private WifiManager.WifiLock wifiLock;
    private PlusController plusController;
    private NetworkCallback callbackGO, callbackDirect;
    private WifiP2pManager.ActionListener mActionListener;
    private boolean bitch = true;
    private Thread t1;

    public ConnectionController(Connection connection, Database database) {
        this.connection = connection;
        encryption = new Encryption(connection);
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        this.database = database;
        setUser();
        myId = myUser.getIdUser();
        amIGO = false;
        myUser.setPublicKey(encryption.convertPublicKeyToString());
        database.setPublicKey(encryption.convertPublicKeyToString());
        tcpClient = new TcpClient(database, encryption, connection);
        multicastP2P = new Multicast_P2P(database, this, tcpClient, connection);
        multicastWLAN = new Multicast_WLAN(database, this, tcpClient, connection);
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        bluetoothAdvertiser = new BluetoothAdvertiser();
        bluetoothScanner = new BluetoothScanner(connection, this, bluetoothAdvertiser);
        mConfig = new WifiP2pConfig.Builder()
                .setNetworkName(SSID + myId)
                .setPassphrase(networkPassword)
                .setGroupOperatingBand(WifiP2pConfig.GROUP_OWNER_BAND_2GHZ)
                .build();
        connManager = (ConnectivityManager) connection.getSystemService(Context.CONNECTIVITY_SERVICE);
        tcpServer = new TcpServer(connection, database, encryption, tcpClient);
        ChatController chatController = new ChatController().newIstance(database, tcpClient, multicastP2P, multicastWLAN, this);
        MessageListener messageListener = new MessageListener().newInstance(connection.getApplicationContext(), database, chatController);
        wifiLock = wifiManager.createWifiLock(1, "testLock");
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                disconnectToGroup();
                //android.os.Process.killProcess(android.os.Process.myPid());
            }
        }));


    }

    /**
     * Remove my group
     */
    public void removeGroup() {
        bluetoothAdvertiser.stopAdvertising();
        wifiLock.release();
        System.out.println("MCHANNEL: " + mChannel);
        mManager.removeGroup(mChannel, null);
        try {
            mChannel.close();
        } catch (Throwable e) {
            System.out.println("Direct-Connection closed");
        }
    }

    /**
     * Create a new group
     */
    @SuppressLint("MissingPermission")
    public void createGroup() {
        GO_leave = false;
        amIGO = true;
        mManager.createGroup(mChannel, mConfig, mActionListener = new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                if (bitch && myUser != null) { //non si sa il motivo ma dopo on failure riattiva l'onsuccess
                    bluetoothAdvertiser.stopAdvertising();
                    bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceGroupOwner, myId);
                    bluetoothAdvertiser.startAdvertising();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (myUser != null) {
                                try {
                                    while (MyNetworkInterface.p2pIpv6Address.equals("")) {
                                        MyNetworkInterface.setNetworkInterfacesNames();
                                    }
                                    myUser.setInetAddressP2P(MyNetworkInterface.p2pIpv6Address);
                                    database.setMyGroupOwnerIp(MyNetworkInterface.p2pIpv6Address, myUser.getIdUser());
                                } catch (SocketException | UnknownHostException e) {
                                    e.printStackTrace();
                                }
                                multicastP2P.createMultigroupP2P();
                                if (wifiManager.getConnectionInfo().getSSID().contains("DIRECT-CONNECTION")) {
                                    multicastP2P.setMulticastWlan(multicastWLAN.getMulticastWlan());
                                    multicastWLAN.setMulticastP2P(multicastP2P.getMulticastP2P());
                                }
                                t1 = new Thread(multicastP2P);
                                t1.start();
                                bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId);
                                if (!tcpServer.isRunning())
                                    tcpServer.setup();
                                tcpServer.setMulticastP2p(multicastP2P);
                                wifiLock.acquire();
                            }
                        }
                    }, 3000);
                }
            }

            @Override
            public void onFailure(int reason) {
                bitch = false;
                System.out.println("create group error" + reason);
                tcpServer.close();
                //multicastP2P.createMultigroupP2P();
                //multicastP2P.closeMultigroupP2p();
                mManager.removeGroup(mChannel, mActionListener);
                mChannel.close();
                new CountDownTimer(3000, 3000) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
                        bitch = true;
                        createGroup();
                    }
                }.start();

            }
        });
    }

    /**
     * Connect to a group when i'm a own a group
     */
    public void connectToGroupWhenGroupOwner(String id) {//GroupOwner groupOwner){//
        tcpServer.close();
        tcpServer = new TcpServer(connection, database, encryption, tcpClient);
        wifiConnection(id);
        connManager.requestNetwork(networkRequest, callbackGO = new NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                if (!wifiManager.getConnectionInfo().getSSID().contains("DIRECT-CONNECTION"))
                    wifiConnection(id);
                else {
                    if (database.getMyInformation() != null) {
                        new CountDownTimer(5000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                if (myUser != null) {
                                    try {
                                        while (MyNetworkInterface.wlanIpv6Address.equals("")) {
                                            MyNetworkInterface.setNetworkInterfacesNames();
                                        }
                                        myUser.setInetAddressWlan(MyNetworkInterface.wlanIpv6Address);
                                        database.setIp(myUser.getIdUser(), myUser.getInetAddressWlan().getHostAddress());
                                    } catch (SocketException | UnknownHostException e) {
                                        e.printStackTrace();
                                    }
                                    if (!tcpServer.isRunning())
                                        tcpServer.setup();
                                    tcpServer.setMulticastP2p(multicastP2P);
                                    multicastWLAN.createMulticastSocketWlan0();
                                    multicastP2P.setMulticastWlan(multicastWLAN.getMulticastWlan());
                                    multicastWLAN.setMulticastP2P(multicastP2P.getMulticastP2P());
                                    Thread t1 = new Thread(multicastWLAN);
                                    t1.start();
                                    multicastWLAN.sendAllMyGroupInfo();
                                }
                            }
                        }.start();
                    }
                }
            }
        });
    }

    /**
     * Connect to a group
     */
    public void connectToGroup(final String id) {
        wifiConnection(id);
        bluetoothAdvertiser.stopAdvertising();
        bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceClientConnectedToGroupOwner, id);
        bluetoothAdvertiser.stopAdvertising();
        connManager.requestNetwork(networkRequest, callbackDirect = new NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                if (myUser != null) {
                    new CountDownTimer(100, 10) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            if (myUser != null) {
                                if (!wifiManager.getConnectionInfo().getSSID().contains("DIRECT-CONNECTION"))
                                    wifiConnection(id);
                                else {
                                    if (!tcpServer.isRunning())
                                        tcpServer.setup();
                                    try {
                                        while (MyNetworkInterface.wlanIpv6Address.equals("")) {
                                            MyNetworkInterface.setNetworkInterfacesNames();
                                        }
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
                        }
                    }.start();
                }
            }
        });
    }

    /**
     * Disconnected to a group
     */
    public void disconnectToGroup() {
        tcpServer.close();
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null) {
            GOLeaves();
        }
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null) {
            multicastWLAN.imLeaving();
        }
        if (callbackGO != null)
            connManager.unregisterNetworkCallback(callbackGO);
        if (callbackDirect != null)
            connManager.unregisterNetworkCallback(callbackDirect);
        bluetoothAdvertiser.stopAdvertising();
    }

    /**
     * measure the power connection between me and the group owner if it is to low i'm going to disconnect
     */
    public void autoDisconnect() {
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
        if (level <= 1 && !GO_leave) {
            disconnectToGroup();
        }
    }

    /**
     * The group owner is leaving the group :(
     */
    private void GOLeaves() {
        multicastP2P.sendGlobalMsg("GO_LEAVES_BYE£€".concat(database.getMaxId() == null ? "" : database.getMaxId()));
        int prec = LocalDateTime.now().getSecond();
        while (LocalDateTime.now().getSecond() - prec < 10) ;
        multicastP2P.closeMultigroupP2p();
        this.removeGroup();
        mChannel.close();
        //TODO pulire dal db gli utenti connessi a me
    }

    /**
     * Starting to look around to check for group owners
     */
    public void initProcess() {
        Connection.initProcessStarted = true;
        bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceLookingForGroupOwner, null);
        bluetoothAdvertiser.startAdvertising();
        bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwner);
    }

    public void reScan() {
        if (amIGO)
            bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId);
        else
            bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwner);
    }

    /**
     * GROUP OWNER IS LEAVING SO I NEED TO CONNECT TO ANOTHER ONE, WHICH ID WAS GIVEN TO ME
     *
     * @param id id to which i will connect
     */
    public void connectToGroupOwnerId(String id) {
        GO_leave = false;
        bluetoothScanner.setClientToRequestGroupId(id);
        bluetoothScanner.initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId);
    }

    //TESTING DISCONNECTION

    /**
     * set my user retrieving the info from db
     */
    private void setUser() {
        String[] info = database.getMyInformation();
        if (database.getMyInformation() != null) {
            myUser = new User(info[0], info[1], info[2], info[3], info[4], info[5], info[6], info[7], info[8], info[9], info[10]);
            myUser.setProfilePicBase64(myUser.getProfilePic());
        }

    }

    /**
     * connect with wifi to a certain group owner
     *
     * @param id group owner to which i will connect
     */
    public void wifiConnection(String id) {
        NetworkSpecifier specifier =
                new WifiNetworkSpecifier.Builder()
                        .setSsid(SSID + id)
                        .setWpa2Passphrase(networkPassword)
                        .build();
        networkRequest = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .setNetworkSpecifier(specifier)
                .build();
    }

    /**
     * get SSID
     */
    public String getSSID() {
        return wifiManager.getConnectionInfo().getSSID();
    }

    /**
     * get wifi manager
     */
    public WifiManager getWifiManager() {
        return wifiManager;
    }

}