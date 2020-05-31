package com.example.connection.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.ParcelUuid;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.UUID;

public class Bluetooth {
    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    AdvertiseData mAdvertiseData;
    AdvertiseSettings mAdvertiseSettings;
    AdvertiseCallback mAdvertiseCallback;

    public Bluetooth () {
         mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
    }
    protected void setAdvertiseData() {
        ParcelUuid pUuid = new ParcelUuid( UUID.fromString(( "CDB7950D-73F1-4D4D-8E47-C090502DBD63" ) ));
        mAdvertiseData = new AdvertiseData.Builder()
                .setIncludeDeviceName( true )
                .addServiceUuid( pUuid )
                .addServiceData( pUuid, "nomewifidirect".getBytes( Charset.forName( "UTF-8" ) ) )
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
