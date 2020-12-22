package com.example.connection.Device_Connection;

import android.annotation.SuppressLint;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.Model.GroupOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ServiceConnections {

    private ArrayList<GroupOwner> groupOwners;
    private ArrayList<String> clientConnectedToGO,idRequestedToBeGO;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private String serviceName = "connection", serviceType;
    private static final String TAG = "connection_GO";
    private TextView logView = null;
    private static final int logViewID = View.generateViewId();
    private String backlog = "";
    private Database database;
    private String myId = database.getMyInformation()[0];

    public ServiceConnections(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, Database database) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.database = database;
        groupOwners = new ArrayList<GroupOwner>();
        clientConnectedToGO = new ArrayList<String>();
    }

    /**
     * Unregister this service.
     */
    public void unregisterService() {
        mManager.clearLocalServices(mChannel, null);
    }

    /**
     * Start looking for peer services.
     */
    public void setupServiceDiscovery() {
        /** Setup listeners for the Bonjour services */
        mManager.setDnsSdResponseListeners(mChannel, new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice wifiDirectDevice) {
                serviceType = registrationType;

            }
        }, new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
                if (fullDomain.trim().equals("connection.service_type_bonjour.local.")) {
                    switch (serviceType) {
                        default:
                            break;
                        case Task.ServiceEntry.serviceGroupOwner: //FOR CREATE, CONNECT GO
                            groupOwners.add(new GroupOwner(record.get("ConnectionID").toString(), record.get("SSID").toString(), record.get("networkPassword").toString()));
                            break;
                        case Task.ServiceEntry.serviceClientConnectedToGroupOwner: //CLIENTS CONNECTED TO A SPECIFIED GROUP OWNER
                            clientConnectedToGO.add(record.get("ConnectionID").toString());
                            break;
                        case Task.ServiceEntry.serviceRequestClientBecomeGroupOwner: //REQUEST A CLIENT TO BECOME GO
                            idRequestedToBeGO.add(record.get("ConnectionID").toString());
                            break;
                    }
                }

            }
        });
        addServiceRequest(WifiP2pDnsSdServiceRequest.newInstance());
    }

    @SuppressLint("MissingPermission")
    public void startServiceDiscovery() {
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

    /**
     * Stop searching for peer services
     */
    private void stopServiceDiscovery() {
        mManager.clearServiceRequests(mChannel, null);
    }

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

    //PART GROUP OWNER -----------------------------------------------------------------------------------------------------------------
    //Register a group owner
    @SuppressLint("MissingPermission")
    public void registerService(String serviceType, String myId, String SSID, String networkPassword) {

        unregisterService();

        /** Create a string map containing information about your service. */
        Map<String, String> record = new HashMap<String, String>();
        record.put("ConnectionID", myId);
        record.put("SSID", SSID);
        record.put("networkPassword", networkPassword);

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

    //RETURN THE NEAREST GROUPOWNER WITH A ID GREATEST THAN YOURS
    public GroupOwner findOtherGroupOwner() {
        final GroupOwner[] groupOwner = new GroupOwner[1];
        final Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        startServiceDiscovery();
                        for (int i = 0; i < groupOwners.size(); i++) {
                            if (myId.compareTo(groupOwners.get(i).getId()) < 0) {
                                groupOwner[0] = groupOwners.get(i);
                                interrupt();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return groupOwner[0];
    }

    //PART CLIENT ---------------------------------------------------------------------------------------------------
    //Register a client connected to a group owner
    @SuppressLint("MissingPermission")
    public void registerService(String serviceType, String myId, String idGroupOwner) {

        unregisterService();

        /** Create a string map containing information about your service. */
        Map<String, String> record = new HashMap<String, String>();
        record.put("ConnectionID", myId);
        record.put("IdGroupOwner", idGroupOwner);

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

    //listening to the near client, if i find my id, i have to become a GO
    public boolean clientListeningOtherClient() {
        final Thread thread = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        sleep(1000);
                        startServiceDiscovery();
                        if(!idRequestedToBeGO.isEmpty()){
                            for (int i=0; i<idRequestedToBeGO.size();i++){
                                if(myId.equals(idRequestedToBeGO)){
                                    interrupt();
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    //PART REQUEST A CLIENT TO BECOME GROUP-OWNER
    @SuppressLint("MissingPermission")
    public void registerService(String serviceType, String nearClientId) {

        unregisterService();

        /** Create a string map containing information about your service. */
        Map<String, String> record = new HashMap<String, String>();
        record.put("ConnectionID", nearClientId);

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
                startServiceDiscovery();
            }

            @Override
            public void onFailure(int arg0) {
                logd("addLocalService.onFailure: " + arg0);
            }
        });
    }

    //FIND THE NEAREST DEVICE ID FROM A CLIENT WHICH IS CONNECTED TO A GROUP OWNER, AND ASK HIM TO BECOME GROUP OWNER
    public Optional<GroupOwner> searchAndRequestForIdNetwork() {
        registerService(Task.ServiceEntry.serviceLookingForGroupOwner, myId, null);
        Thread thread = new Thread(){
                @Override
                public void run() {
                    while (true) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startServiceDiscovery();
                    if (!clientConnectedToGO.isEmpty()) {
                        registerService(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner, clientConnectedToGO.get(0));
                        interrupt();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return lookingForGroupOwner(clientConnectedToGO.get(0));
    }

    //CALLED AFTER I REQUEST A SPECIFIED DEVICE TO BECOME GO
    public Optional<GroupOwner> lookingForGroupOwner(final String id) {
        final int[] j = {0};
        Thread thread = new Thread(){
            @Override
            public void run() {
                int count=0;
                while (true) {
                    try {
                        sleep(1000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startServiceDiscovery();

                    if (!groupOwners.isEmpty()) {
                        for (int i =0; i<groupOwners.size();i++){
                            if(groupOwners.get(i).getId().equals(id)){
                                j[0] =i;
                                interrupt();
                            }
                        }
                        if(count>10){
                            j[0]=0;
                            interrupt();
                        }
                    }

                    if(count>10){
                        interrupt();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.of(groupOwners.get(j[0]));
    }

    //FIRST METHOD TO BE CALLED WHEN THE APP STARTS, WITH THIS THE DEVICE START LOOKING FOR A GO
    public Optional<GroupOwner> lookingForGroupOwner() {
        registerService(Task.ServiceEntry.serviceLookingForGroupOwner, myId, null);
        Thread thread = new Thread(){
            @Override
            public void run() {
                int count=0;
                while (true) {
                    try {
                        sleep(1000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startServiceDiscovery();
                    if (!groupOwners.isEmpty() || count > 10) {
                        interrupt();
                    }
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Optional.of(groupOwners.get(0));
    }
}
