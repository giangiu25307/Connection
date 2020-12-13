package com.example.connection.Device_Connection;

import android.annotation.SuppressLint;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.Model.GroupOwner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceConnections {

    private ArrayList<GroupOwner> groupOwners;
    //private HashMap<String, WifiP2pDevice> devices;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private String serviceName = "connection",serviceType;
    private static final String TAG = "connection_GO";
    private TextView logView = null;
    private static final int logViewID = View.generateViewId();
    private String backlog = "";
    Database database;

    public ServiceConnections(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, Database database){
        this.mManager=mManager;
        this.mChannel=mChannel;
        this.database = database;
        groupOwners = new ArrayList<>();
        //devices = new HashMap<String, WifiP2pDevice>();
    }

    @SuppressLint("MissingPermission")
    public void registerService(String serviceType, String myId, String SSID, String networkPassword) {

        unregisterService();

        /** Create a string map containing information about your service. */
        Map<String, String> record = new HashMap<String, String>();
        record.put("ConnectionID", myId);
        record.put("SSID",SSID);
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

    @SuppressLint("MissingPermission")
    public void registerService(String serviceType, String nearDeviceId) {

        unregisterService();

        /** Create a string map containing information about your service. */
        Map<String, String> record = new HashMap<String, String>();
        record.put("ConnectionID",nearDeviceId);

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
                serviceType=registrationType;

            }
        }, new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain, Map record, WifiP2pDevice device) {
                if (fullDomain.trim().equals("connection.service_type_bonjour.local.")){
                    //devices.put(record.get("ConnectionID").toString(), device);
                    switch(serviceType){
                        default: break;
                        case Task.ServiceEntry.serviceGroupOwner:
                            groupOwners.add(new GroupOwner(record.get("ConnectionID").toString(),record.get("SSID").toString(),record.get("networkPassword").toString()));
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
                //verificare se in questo istante il dispositivo viene visto, controllare con un if se qualcuno della lista e GO
                // senno connettersi al primo perche si presuppone che vada in base alla distanza
                //se non trova nessuno allora crea il servizio e diventa GO
                //se trovi un dispositivo non GO si valuta come sempre id del servizio

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


    public void createOwnerRequestThread(final String nearDeviceId) {
        registerService(Task.ServiceEntry.serviceIdBecomeGroupOwner,nearDeviceId);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void searchForIdNetwork(String id){
        /*while(true){
            startServiceDiscovery();
            if (!devices.isEmpty()) {
                for (Map.Entry<String, WifiP2pDevice> entryDevice : devices.entrySet()) {
                    for (Map.Entry<String, String> entryServices : services.entrySet()) {
                        if(entryDevice.getKey().equals(entryServices.getKey())){
                            //connectionToGroup(entryDevice.getValue().deviceAddress);
                        }
                    }
                }
            }
        }*/

    }

    //RETURN THE NEAREST GROUPOWNER WITH A ID GREATEST THAN YOURS
    public GroupOwner findOtherGroupOwner(){
        final GroupOwner[] groupOwner = new GroupOwner[1];
        final Thread thread=new Thread(){
            @Override
            public void run() {
                String myId= database.getMyInformation()[0];
                while(true) {
                    try {
                        sleep(1000);
                        startServiceDiscovery();
                        for (int i=0;i< groupOwners.size();i++){
                            if(myId.compareTo(groupOwners.get(i).getId())<0){
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
        return groupOwner[0];
    }
}
