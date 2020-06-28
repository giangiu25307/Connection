package com.example.connection.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothAdvertiser {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    AdvertiseData mAdvertiseData;
    AdvertiseSettings mAdvertiseSettings;
    AdvertiseCallback mAdvertiseCallback;

    public BluetoothAdvertiser(String nome) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        setAdvertiseData(nome);
        setAdvertiseSettings();
        mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }


            @Override
            public void onStartFailure(int errorCode) {
                System.out.println(  "??????????????Advertising onStartFailure: " + errorCode );
                super.onStartFailure(errorCode);
            }
        };
    }
    protected void setAdvertiseData(String nome) {
        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(( "CDB7950D-73F1-4D4D-8E47-C090502DBD63" ) ));
        mAdvertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName( false )
                .addServiceData( pUuid, nome.getBytes(StandardCharsets.UTF_8) )
                .setIncludeTxPowerLevel(false)
                .build();
    }


    protected void setAdvertiseSettings() {
        mAdvertiseSettings = new AdvertiseSettings.Builder()
                .setAdvertiseMode( AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY )
                .setTxPowerLevel( AdvertiseSettings.ADVERTISE_TX_POWER_HIGH )
                .setConnectable( false )
                .build();
    }
    public void startBLE(){

        mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSettings, mAdvertiseData, mAdvertiseCallback);

    }
}
