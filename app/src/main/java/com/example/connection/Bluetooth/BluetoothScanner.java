package com.example.connection.Bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.ParcelUuid;
import android.text.TextUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BluetoothScanner extends ListActivity {
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler = new Handler();
    ScanSettings settings;
    public BluetoothScanner(){
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
       /* ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid( new ParcelUuid(UUID.fromString( "" ) ) )
                .build();
        filters.add( filter );*/
        settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .build();

    }
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if( result == null || result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName())){
                System.out.println("ciao");
                return;
            }

            StringBuilder builder = new StringBuilder( result.getDevice().getName() );

            builder.append("\n").append(new String(result.getScanRecord().getServiceData(result.getScanRecord().getServiceUuids().get(0)), Charset.forName("UTF-8")));
            System.out.println(builder);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            System.out.println( "Discovery onScanFailed: " + errorCode );
            super.onScanFailed(errorCode);
        }
    };
    public void startBLEScan(){
        mBluetoothLeScanner.startScan(null, settings, mScanCallback);
    }
}
