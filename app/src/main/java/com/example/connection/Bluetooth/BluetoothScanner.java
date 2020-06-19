package com.example.connection.Bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class BluetoothScanner extends ListActivity {
    private BluetoothLeScanner mBluetoothLeScanner;
    ScanSettings settings;
    private List<String> devices=new ArrayList<>();
    public BluetoothScanner(){
        mBluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
       /* ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid( new ParcelUuid(UUID.fromString( "" ) ) )
                .build();
        filters.add( filter );*/
        settings = new ScanSettings.Builder()
                .setScanMode( ScanSettings.SCAN_MODE_LOW_LATENCY )
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
                .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
                .build();

    }
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if( result == null || result.getDevice() == null){
                return;
            }
            Set keys = result.getScanRecord().getServiceData().keySet();

            for (Iterator i = keys.iterator(); i.hasNext();) {
                byte[] array=  result.getScanRecord().getServiceData().get(i.next());
                StringBuilder sb = new StringBuilder();
                StringBuilder output = new StringBuilder();
                for (byte b : array) {
                    output.append((char) Integer.parseInt(String.format("%02X", b), 16));
                }
                System.out.println(output.toString());
                devices.add(output.toString());
            }

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

    public List<String> getBleDevices(){
        return devices;
    }
}