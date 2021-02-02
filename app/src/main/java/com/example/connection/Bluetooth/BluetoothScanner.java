package com.example.connection.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.nio.charset.StandardCharsets;
import java.util.Collection;


public class BluetoothScanner {
    final BluetoothManager bluetoothManager ;
    BluetoothAdapter bluetoothAdapter;
    public BluetoothScanner(MainActivity mainActivity) {
        bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
    }

        private BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        private boolean mScanning;
        private Handler handler = new Handler();
// Stops scanning after 10 seconds.
        private static final long SCAN_PERIOD = 300000;
        public void scanLeDevice() {
            if (!mScanning) {
                // Stops scanning after a pre-defined scan period.
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        bluetoothLeScanner.stopScan(leScanCallback);
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                bluetoothLeScanner.startScan(leScanCallback);
                System.out.println("cisono");
            } else {
                mScanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
            }
        }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                       Collection<byte[]> prova= result.getScanRecord().getServiceData().values();
                       byte[] prova1= new byte[prova.size()];
                        for (byte[] e: prova){
                            prova1=e;

                        }
                        String s = new String(prova1, StandardCharsets.UTF_8);
                    System.out.println(s);




                }
            };
    }



