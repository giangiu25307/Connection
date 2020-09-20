package com.example.connection.Controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pUpnpServiceRequest;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.connection.Model.User;
import com.example.connection.TCP_Connection.MultiThreadedServer;
import com.example.connection.TCP_Connection.TCP_Client;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;
import com.example.connection.View.WiFiDirectBroadcastReceiver;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    int count = 0;
    Collection<WifiP2pDevice> peers;
    List<WifiP2pDevice> newList;
    HashMap<String, String> macAdresses;
    InetAddress groupOwnerAddress;
    WifiP2pManager.PeerListListener peerListListener;
    boolean DeviceFound;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    WifiP2pDevice GroupOwner;
    private String serviceName = "connection";
    private static final String serviceType = "_presence._tcp";
    private static final String TAG                 = "prova";
    private TextView            logView             = null;
    private static final int    logViewID           = View.generateViewId();
    private String              backlog             = "";

    public ConnectionController(Connection connection, Database database, User user) {
        this.connection = connection;
        mManager = (WifiP2pManager) connection.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(connection, connection.getMainLooper(), null);
        ConnectionToDevice = "";
        ConnectionStatus = "";
        this.database = database;
        this.user = user;
        wifiManager = (WifiManager) connection.getSystemService(Context.WIFI_SERVICE);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        connection.registerReceiver(wifiScanReceiver, intentFilter);
        macAdresses = new HashMap<>();
        peers = new ArrayList<WifiP2pDevice>();
        newList = new ArrayList<WifiP2pDevice>();
        boolean DeviceFound = true;
        GroupOwner = new WifiP2pDevice();
        SearchPeers();
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, peerListListener, connectionInfoListener);
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
            tcpClient.startConnection("192.168.49.1", 50000);
        }
    }

    //Remove a group --------------------------------------------------------------------------------------------------------------------------------
    private void removeGroup() {
        mManager.removeGroup(mChannel, null);
    }

    //Create a group --------------------------------------------------------------------------------------------------------------------------------
    public void createGroup() {
        System.out.println("create group");

        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reason) {
                System.out.println(reason);
            }
        });

    }

    public void GetDeviceName() {
        mManager.requestGroupInfo(mChannel, new WifiP2pManager.GroupInfoListener() {
            @Override
            public void onGroupInfoAvailable(WifiP2pGroup group) {
                WifiP2pDevice device = group.getOwner();
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
                System.out.println(res1.toString());
            }
        } catch (Exception ex) {
            //handle exception
        }
        return;
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
        config = new WifiP2pConfig();
        config.deviceAddress = GroupOwner.deviceAddress;
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
        if (level <= 2) {
            disconnectToGroup();
        }
    }

    //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
    public void GOLeaves() {
        final String maxId = database.getMaxId();

        tcpClient.startConnection(database.findIp(maxId), 50000);
        tcpClient.sendMessage("GO_LEAVES_BY£€", "");
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                udpClient.sendGlobalMsg("GO_LEAVES_BYE£€" + maxId);
            }
        }.start();
        this.removeGroup();

    }

    //return the all client list --------------------------------------------------------------------------------------------------------------------------------
    public Optional<Cursor> getAllClientList() {

        return Optional.of(database.getAllUsers());

    }


    public void ServiceGroupOwner() {
        Map record = new HashMap();
        record.put("listenport", String.valueOf(10000));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");

        // Service information.  Pass it an instance name, service type
        // _protocol._transportlayer , and the map containing
        // information other devices will want once they connect to this one.
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance("_test", "_presence._tcp", record);
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int arg0) {
                System.out.println(arg0);
            }
        });
    }

    public String Discovery() {
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
                        System.out.printf(device.deviceName);
                        System.out.println(device.deviceAddress);
                        if (device.deviceAddress.equals("32:07:4d:89:4c:f3")) {
                            GroupOwner = device;
                            connectionToGroup();
                        }
                    }
                }

            }
        };
        return DeviceFound;
    }

    public String ConnectionListener() {
        //SERVE SOLO A CAPIRE CHI è HOST O CLIENT, DA RIMUOVERE PER CREARE UNA VERA E PROPRIA CHAT-------------------------------------------------------------------------------------------------------------------
        connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                groupOwnerAddress = wifiP2pInfo.groupOwnerAddress;
                if (wifiP2pInfo.groupFormed && wifiP2pInfo.isGroupOwner) {
                    ConnectionStatus = "Host";
                } else if (wifiP2pInfo.groupFormed) {
                    ConnectionStatus = "Client";
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

    public void registerService() {

        unregisterService();

        /** Create a string map containing information about your service. */
        Map<String, String> record = new HashMap<String, String>();
        record.put("DeviceID", Settings.Secure.getString(connection.getContentResolver(), Settings.Secure.ANDROID_ID));

        /**
         * Service information. Pass it an instance name, service type
         * _protocol._transportlayer , and the map containing information other
         * devices will want once they connect to this one.
         */
        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(serviceName, serviceType, record);

        /**
         * Add the local service, sending the service info, network channel, and
         * listener that will be used to indicate success or failure of the
         * request.
         */
        mManager.addLocalService(mChannel, serviceInfo, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logd("addLocalService.onSuccess");
            }

            @Override
            public void onFailure(int arg0) {
                logd("addLocalService.onFailure: " + arg0);
            }
        });
    }

    /** Unregister this service. */
    private void unregisterService() {
       mManager.clearLocalServices(mChannel, null);
    }

    /** Start looking for peer services. */
    public void startServiceDiscovery() {
         stopServiceDiscovery();

        /** Setup listeners for vendor-specific services. */
        mManager.setServiceResponseListener(mChannel, new WifiP2pManager.ServiceResponseListener() {

            @Override
            public void onServiceAvailable(int protocolType, byte[] responseData, WifiP2pDevice srcDevice) {
                logd("onServiceAvailable: protocolType:" + protocolType + ", responseData: " + responseData.toString()
                        + ", WifiP2pDevice: " + srcDevice.toString());
            }
        });

        /** Setup listeners for the Bonjour services */
        mManager.setDnsSdResponseListeners(mChannel, new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                WifiP2pDevice wifiDirectDevice) {
                logd("onDnsSdServiceAvailable: instanceName:" + instanceName + ", registrationType: " + registrationType
                        + ", WifiP2pDevice: " + wifiDirectDevice.toString());
            }
        }, new WifiP2pManager.DnsSdTxtRecordListener() {

            @SuppressWarnings("rawtypes")
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
                System.out.println(device.isGroupOwner());
                logd("onDnsSdTxtRecordAvailable: fullDomain: " + fullDomain + ", record: " + record.toString()
                        + ", WifiP2pDevice: " + device.toString());
            }
        });

        /** Setup listeners for Upnp services */
        mManager.setUpnpServiceResponseListener(mChannel, new WifiP2pManager.UpnpServiceResponseListener() {

            @Override
            public void onUpnpServiceAvailable(List<String> uniqueServiceNames, WifiP2pDevice srcDevice) {
                logd("onUpnpServiceAvailable: uniqueServiceNames:" + uniqueServiceNames.toString() + ", WifiP2pDevice: "
                        + srcDevice.toString());
            }
        });

        /** Register to receive all possible types of service requests. */
        addServiceRequest(WifiP2pServiceRequest.newInstance(WifiP2pServiceInfo.SERVICE_TYPE_ALL));
        addServiceRequest(WifiP2pDnsSdServiceRequest.newInstance());
        addServiceRequest(WifiP2pUpnpServiceRequest.newInstance());
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                logd("discoverServices.onSuccess()");
            }

            @Override
            public void onFailure(int code) {
                logd("discoverServices.onFailure: " + code);
            }
        });
    }

    /** Stop searching for peer services */
    private void stopServiceDiscovery() {
        mManager.clearServiceRequests(mChannel, null);
    }

    /**
     * Add the service discovery request for {@link WifiP2pServiceRequest}s of
     * the specified type.
     */
    private void addServiceRequest(final WifiP2pServiceRequest request) {
          mManager.addServiceRequest(mChannel, request, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                logd("addServiceRequest.onSuccess() for requests of type: " + request.getClass().getSimpleName());
            }

            @Override
            public void onFailure(int code) {
                logd("addServiceRequest.onFailure: " + code + ", for requests of type: "
                        + request.getClass().getSimpleName());
            }
        });
    }

    private void logd(String loggable) {
        Log.d(TAG, loggable);
        if (logView != null) {
            if (!backlog.isEmpty()) {
                loggable = append(backlog, loggable);
                backlog = "";
            }
            final String toLog = loggable;
        } else {
            backlog = append(backlog, loggable);
        }
    }

    private String append(String a, String b) {
        return a + "\n\n" + b;
    }
}
