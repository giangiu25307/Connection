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

import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.Model.GroupOwner;
import com.example.connection.View.Connection;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;


public class BluetoothScanner {

    final BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;
    private String idGroupOwner, serviceType, idHostingService, identifierApp;

    private BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private Handler handler = new Handler();
    private Database database;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public BluetoothScanner(Connection connection, Database database) {
        bluetoothManager = (BluetoothManager) connection.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        this.database = database;
        resetVariables();
    }

    private void resetVariables() {
        idGroupOwner = "";
        serviceType = "";
        idHostingService = "";
        identifierApp = "";
    }

    //FIND THE NEAREST DEVICE ID FROM A CLIENT WHICH IS CONNECTED TO A GROUP OWNER, AND ASK HIM TO BECOME GROUP OWNER
    public Optional<String[]> searchAndRequestForIdNetwork() {
        resetVariables();
        final String data[] = new String[3];
        Thread thread = new Thread() {
            @Override
            public void run() {
                boolean stop = false;
                int count = 0;
                while (!stop) {
                    try {
                        sleep(1000);
                        count++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    bluetoothLeScanner.startScan(leScanCallback);
                    if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceClientConnectedToGroupOwner)) {
                        data[0] = idHostingService;
                        data[1] = serviceType;
                        data[2] = idGroupOwner;
                        interrupt();
                        stop = true;
                    }
                    if (count > 1) {
                        interrupt();
                        stop = true;
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
        if (data[0] != null) return Optional.of(data);
        else return Optional.empty();
    }

    //listening to the near client, if i find my id, i have to become a GO
    public boolean clientListeningOtherClient() {
        resetVariables();
        final Thread thread = new Thread() {
            boolean stop = false;

            @Override
            public void run() {
                while (!stop) {
                    try {
                        bluetoothLeScanner.startScan(leScanCallback);
                        sleep(1000);
                        if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner) && idGroupOwner.trim().equals(database.getMyInformation()[0])) {
                            bluetoothLeScanner.stopScan(leScanCallback);
                            interrupt();
                            stop = true;
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

    //find the nearest Connection groupOwner device
    public Optional<String[]> lookingForGroupOwner() {
        resetVariables();
        final String data[] = new String[3];
        final Thread thread = new Thread() {
            boolean stop = false;
            int count=0;

            @Override
            public void run() {
                while (!stop) {
                    try {
                        bluetoothLeScanner.startScan(leScanCallback);
                        sleep(1000);
                        if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner)) {
                            data[0] = idHostingService;
                            data[1] = serviceType;
                            data[2] = idGroupOwner;
                            stop = true;
                            bluetoothLeScanner.stopScan(leScanCallback);
                        }
                        if(count>9){
                            stop=true;
                        }
                        count++;
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
        if (data[0] != null) return Optional.of(data);
        return Optional.empty();
    }

    public Optional<String[]> lookingForGroupOwner(final String id) {
        resetVariables();
        final String data[] = new String[3];
        final Thread thread = new Thread() {
            boolean stop = false;
            int count=0;

            @Override
            public void run() {
                while (!stop) {
                    try {
                        bluetoothLeScanner.startScan(leScanCallback);
                        sleep(1000);
                        if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().equals(id)) {
                            data[0] = idHostingService;
                            data[1] = serviceType;
                            data[2] = idGroupOwner;
                            stop = true;
                            bluetoothLeScanner.stopScan(leScanCallback);
                            if (count>9){
                                stop=true;
                            }
                            count++;
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
        if (data[0] != null) return Optional.of(data);
        return Optional.empty();
    }

    //Scan for the groupOwner near me with greater id than mine
    public String[] findOtherGroupOwner() {
        resetVariables();
        final String data[] = new String[3];
        final Thread thread = new Thread() {
            boolean stop = false;

            @Override
            public void run() {
                while (!stop) {
                    try {
                        bluetoothLeScanner.startScan(leScanCallback);
                        sleep(1000);
                        if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().compareTo(database.getMyInformation()[0]) > 0) {
                            data[0] = idHostingService;
                            data[1] = serviceType;
                            data[2] = idGroupOwner;
                            stop = true;
                            bluetoothLeScanner.stopScan(leScanCallback);
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
        if (data[0] != null) return data;
        return null;
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            Collection<byte[]> scanResult = result.getScanRecord().getServiceData().values();
            byte[] bytesResult = new byte[scanResult.size()];
            for (byte[] e : scanResult) {
                bytesResult = e;
            }
            String stringResult = new String(bytesResult, StandardCharsets.UTF_8);
            for (int i = 0; i < stringResult.length(); i++) {
                if (i < 7) identifierApp += stringResult.charAt(i);
                if (7 <= i && i < 14) idHostingService += stringResult.charAt(i);
                if (14 <= i && i < 17) serviceType += stringResult.charAt(i);
                if (17 <= i) idGroupOwner += stringResult.charAt(i);
            }
        }
    };
}



