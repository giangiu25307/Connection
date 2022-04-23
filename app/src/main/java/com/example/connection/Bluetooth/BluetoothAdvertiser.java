package com.example.connection.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;


public class BluetoothAdvertiser {
    private android.bluetooth.le.AdvertiseCallback advertiseCallback;
    private AdvertisingSet currentAdvertisingSet;
    private AdvertiseData advertiseData;
    private BluetoothLeAdvertiser bluetoothLeAdvertiser;
    private AdvertiseSettings settings;
        private String data="";
    public BluetoothAdvertiser() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothAdapter.setName("CONNECTION");
        bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                .setConnectable(false)
                .build();
        advertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
            }
        };
        AdvertisingSetCallback callback = new AdvertisingSetCallback() {


            @Override
            public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                System.out.println("onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                        + status);
                currentAdvertisingSet = advertisingSet;
            }

            @Override
            public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
                System.out.println("onAdvertisingDataSet() :status:" + status);
            }

            @Override
            public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
                System.out.println("onScanResponseDataSet(): status:" + status);
            }

            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                System.out.println("onAdvertisingSetStopped():");
            }
        };

    }

    /**
     * Set what the other user will see with BLE
     *
     * @param myId id to identify myself
     * @param type type of information i'm showing {'serviceGroupOwner':'SGO', 'serviceClientConnectedToGroupOwner':'CTG',
     *                                              'serviceRequestClientBecomeGroupOwner':'CBG','serviceLookingForGroupOwner':'LFG'
     *                                              'serviceLookingForGroupOwnerWithSpecifiedId':'LGS','serviceLookingForGroupOwnerWithGreaterId':'LGG'
     *                                              'servicePlusSearchingNetwork':'PSN'}
     * @param idGroupOwner id of the group owner i'm connected to. Or my group owner if i'm not connected to anybody and i own a group
     */
    public AdvertiseData setAdvertiseData(String myId, String type, String idGroupOwner) { //idGroupOwner can be null
        int length;
        if(myId.length()<7){
            length=myId.length();
            for (int i=0;i<7-length;i++){
                myId=" "+myId;
            }
        }
        if(idGroupOwner==null)idGroupOwner="";
        if(idGroupOwner.length()<7){
            length = idGroupOwner.length();
            for (int i=0;i<7-length;i++){
                idGroupOwner=" "+idGroupOwner;
            }
        }
         data = "connect"+myId+type+idGroupOwner;

        advertiseData = new AdvertiseData.Builder()
                .setIncludeTxPowerLevel(true)
                .addServiceData(ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB"), data.getBytes())
                .setIncludeDeviceName(false)
                .build();
        return advertiseData;
    }

    /**
     * Become visible to other devices with BLE
     */
    public void startAdvertising(){
        bluetoothLeAdvertiser.startAdvertising(settings, advertiseData, advertiseCallback);
    }

    /**
     * Become invisible to other devices with BLE
     */
    public void stopAdvertising(){
        bluetoothLeAdvertiser.stopAdvertising(advertiseCallback);
    }

    /**
     * get data
     */
    public String getData() {
        return data;
    }

    // After onAdvertisingSetStarted callback is called, you can modify the
    // advertising data and scan response data:
        /*currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder().
                setIncludeDeviceName(true).setIncludeTxPowerLevel(true).build());
        // Wait for onAdvertisingDataSet callback...
        currentAdvertisingSet.setScanResponseData(new
                AdvertiseData.Builder().addServiceUuid(new ParcelUuid(UUID.randomUUID())).build());
        // Wait for onScanResponseDataSet callback...

        // When done with the advertising:
        advertiser.stopAdvertisingSet(callback);*/
}