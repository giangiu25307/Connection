package com.example.connection.Bluetooth;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;

import java.util.ArrayList;


public class BluetoothScanner extends ListActivity {

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    private Handler handler;
    //private LeDeviceListAdapter leDeviceListAdapter; Classe insistente?! grande google sempre un ottimo lavoro
    ArrayList<BluetoothDevice> bluetoothDevices=new ArrayList<BluetoothDevice>();

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothAdapter.startLeScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }


    // Device scan callback.
    private BluetoothAdapter.LeScanCallback leScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bluetoothDevices.add(device);
                            //leDeviceListAdapter.addDevice(device);
                            //leDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };


}
