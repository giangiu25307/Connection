package com.example.connection.Bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.Model.User;
import com.example.connection.View.Connection;

import java.nio.charset.StandardCharsets;
import java.util.Collection;


public class BluetoothScanner {

    final BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private String idGroupOwner, serviceType, idHostingService, identifierApp;

    private BluetoothLeScanner bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler handler = new Handler();
    private String myId, clientToRequestGroupId;
    private ConnectionController connectionController;
    private BluetoothAdvertiser bluetoothAdvertiser;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    public BluetoothScanner(Connection connection, User user, ConnectionController connectionController, BluetoothAdvertiser bluetoothAdvertiser) {
        bluetoothManager = (BluetoothManager) connection.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        myId = user.getIdUser();
        this.connectionController = connectionController;
        this.bluetoothAdvertiser = bluetoothAdvertiser;
        resetVariables();
    }

    public void initScan(String callbackType) {
        resetVariables();
        switch (callbackType) {
            default:
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwner:
                if (!mScanning) {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwner);
                            initScan(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner);
                        }
                    }, SCAN_PERIOD);
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwner);
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwner);
                }
                break;
            case Task.ServiceEntry.serviceRequestClientBecomeGroupOwner:
                if (!mScanning) {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackSearchAndRequestForIdNetwork);
                            connectionController.createGroup();
                        }
                    }, SCAN_PERIOD);
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackSearchAndRequestForIdNetwork);
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackSearchAndRequestForIdNetwork);
                }
                break;
            case Task.ServiceEntry.serviceClientConnectedToGroupOwner:
                if (!mScanning) {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackClientListeningOtherClient);
                            new CountDownTimer(60000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    initScan(Task.ServiceEntry.serviceClientConnectedToGroupOwner);
                                }

                            }.start();
                        }
                    }, SCAN_PERIOD);
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackClientListeningOtherClient);
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackClientListeningOtherClient);
                }
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId:
                if (!mScanning) {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                            initScan(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner);
                        }
                    }, SCAN_PERIOD);
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwnerId);
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                }
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId:
                if (!mScanning) {
                    // Stops scanning after a pre-defined scan period.
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerWithGreaterId);
                            new CountDownTimer(60000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                }

                                public void onFinish() {
                                    initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId);
                                }

                            }.start();
                        }
                    }, SCAN_PERIOD);
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwnerWithGreaterId);
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerWithGreaterId);
                }
                break;
        }
    }

    // Device scan callback to find the nearest Connection groupOwner device who is hosting a group
    private ScanCallback scanCallbackLookingForGroupOwner = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            resetVariables();
            super.onScanResult(callbackType, result);
            Collection<byte[]> scanResult = result.getScanRecord().getServiceData().values();
            byte[] bytesResult = new byte[scanResult.size()];
            for (byte[] e : scanResult) {
                bytesResult = e;
            }
            String stringResult = new String(bytesResult, StandardCharsets.UTF_8);
            System.out.println(stringResult);
            for (int i = 0; i < stringResult.length(); i++) {
                if (i < 7) identifierApp += stringResult.charAt(i);
                if (7 <= i && i < 14) idHostingService += stringResult.charAt(i);
                if (14 <= i && i < 17) serviceType += stringResult.charAt(i);
                if (17 <= i) idGroupOwner += stringResult.charAt(i);
            }
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner)) {
                bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwner);
                connectionController.connectToGroup(idGroupOwner.trim());
            }
        }
    };

    // Device scan callback to find the nearest Connection groupOwner device who i ask to host the group
    private ScanCallback scanCallbackLookingForGroupOwnerId = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            resetVariables();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().equals(clientToRequestGroupId)) {
                bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                connectionController.connectToGroup(idGroupOwner.trim());
            }
        }
    };

    // Device scan callback to find the nearest Connection groupOwner with id greater then mine
    private ScanCallback scanCallbackLookingForGroupOwnerWithGreaterId = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            resetVariables();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().compareTo(myId) > 0) {
                bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                connectionController.connectToGroupWhenGroupOwner(idGroupOwner.trim());
            }
        }
    };

    // Device scan callback to FIND THE NEAREST DEVICE ID FROM A CLIENT WHICH IS CONNECTED TO A GROUP OWNER, AND ASK HIM TO BECOME GROUP OWNER
    private ScanCallback scanCallbackSearchAndRequestForIdNetwork = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            resetVariables();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceClientConnectedToGroupOwner)) {
                bluetoothLeScanner.stopScan(scanCallbackSearchAndRequestForIdNetwork);
                bluetoothAdvertiser.stopAdvertising();
                bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceRequestClientBecomeGroupOwner, idGroupOwner.trim());
                bluetoothAdvertiser.startAdvertising();
                setClientToRequestGroupId(idGroupOwner.trim());
                bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwnerId);
            }
        }
    };

    // Device scan callback to listening near client, if i find my id, i have to become a GO
    private ScanCallback scanCallbackClientListeningOtherClient = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            resetVariables();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner) && idGroupOwner.trim().equals(myId)) {
                bluetoothLeScanner.stopScan(scanCallbackClientListeningOtherClient);
                connectionController.createGroup();
            }
        }
    };

    public void setClientToRequestGroupId(String clientToRequestGroupId) {
        this.clientToRequestGroupId = clientToRequestGroupId;
    }

    private void resetVariables() {
        identifierApp = "";
        idHostingService = "";
        serviceType = "";
        idGroupOwner = "";
    }
}
