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
import com.example.connection.Controller.PlusController;
import com.example.connection.Controller.Task;
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
    private PlusController plusController;
    private BluetoothAdvertiser bluetoothAdvertiser;
    private CountDownTimer countDownTimer;
    // Stops scanning after 3 seconds.
    private static long SCAN_PERIOD = 3000;

    public BluetoothScanner(Connection connection, ConnectionController connectionController, BluetoothAdvertiser bluetoothAdvertiser) {
        bluetoothManager = (BluetoothManager) connection.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        myId = ConnectionController.myUser.getIdUser();
        this.connectionController = connectionController;
        this.bluetoothAdvertiser = bluetoothAdvertiser;
        resetVariables();
    }

    public BluetoothScanner(Connection connection, PlusController plusController, BluetoothAdvertiser bluetoothAdvertiser) {
        bluetoothManager = (BluetoothManager) connection.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        this.plusController = plusController;
        this.bluetoothAdvertiser = bluetoothAdvertiser;
        resetVariables();
    }

    public void initPlusScan(String service) {
        switch (service) {
            default:
                break;
            case Task.ServiceEntry.servicePlusSearchingNetwork:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackPlusSearchOrRequestNetwork);
                    countDownTimer = new CountDownTimer(SCAN_PERIOD, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackPlusSearchOrRequestNetwork);
                            initPlusScan(Task.ServiceEntry.servicePlusSearchingNetwork);
                        }
                    };
                    countDownTimer.start();
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackPlusSearchOrRequestNetwork);
                }
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackPlusLookingForGroupOwnerId);
                    countDownTimer = new CountDownTimer(10000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackPlusLookingForGroupOwnerId);
                            initPlusScan(Task.ServiceEntry.servicePlusSearchingNetwork);
                        }
                    };
                    countDownTimer.start();
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackPlusLookingForGroupOwnerId);
                }
                break;
        }
    }

    public void initScan(String callbackType) {
        resetVariables();
        switch (callbackType) {
            default:
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwner:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwner);
                    countDownTimer = new CountDownTimer(SCAN_PERIOD, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwner);
                            initScan(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner);
                        }
                    };
                    countDownTimer.start();
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwner);
                }
                break;
            case Task.ServiceEntry.serviceRequestClientBecomeGroupOwner:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackSearchAndRequestForIdNetwork);
                    countDownTimer = new CountDownTimer(SCAN_PERIOD, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackSearchAndRequestForIdNetwork);
                            connectionController.createGroup();
                        }
                    };
                    countDownTimer.start();
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackSearchAndRequestForIdNetwork);
                }
                break;
            case Task.ServiceEntry.serviceClientConnectedToGroupOwner:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackClientListeningOtherClient);
                    countDownTimer = new CountDownTimer(SCAN_PERIOD, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
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
                    };
                    countDownTimer.start();
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackClientListeningOtherClient);
                }
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwnerId);
                    countDownTimer = new CountDownTimer(SCAN_PERIOD, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            mScanning = false;
                            bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                            initScan(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner);
                        }
                    };
                    countDownTimer.start();
                } else {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                }
                break;
            case Task.ServiceEntry.serviceLookingForGroupOwnerWithGreaterId:
                if (!mScanning) {
                    mScanning = true;
                    bluetoothLeScanner.startScan(scanCallbackLookingForGroupOwnerWithGreaterId);
                    countDownTimer = new CountDownTimer(SCAN_PERIOD, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
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
                    };
                    countDownTimer.start();
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
            for (int i = 0; i < stringResult.length(); i++) {
                if (i < 7) identifierApp += stringResult.charAt(i);
                if (7 <= i && i < 14) idHostingService += stringResult.charAt(i);
                if (14 <= i && i < 17) serviceType += stringResult.charAt(i);
                if (17 <= i) idGroupOwner += stringResult.charAt(i);
            }
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && mScanning) {
                mScanning = false;
                bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwner);
                countDownTimer.cancel();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().equals(clientToRequestGroupId) && mScanning) {
                mScanning = false;
                bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                countDownTimer.cancel();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().compareTo(myId) > 0 && mScanning) {
                mScanning = false;
                bluetoothLeScanner.stopScan(scanCallbackLookingForGroupOwnerId);
                countDownTimer.cancel();
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceClientConnectedToGroupOwner) && mScanning) {
                mScanning = false;
                bluetoothLeScanner.stopScan(scanCallbackSearchAndRequestForIdNetwork);
                countDownTimer.cancel();
                bluetoothAdvertiser.stopAdvertising();
                bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceRequestClientBecomeGroupOwner, idGroupOwner.trim());
                bluetoothAdvertiser.startAdvertising();
                setClientToRequestGroupId(idGroupOwner.trim());
                initScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId);
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
            if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceRequestClientBecomeGroupOwner) && idGroupOwner.trim().equals(myId) && mScanning) {
                mScanning = false;
                bluetoothLeScanner.stopScan(scanCallbackClientListeningOtherClient);
                countDownTimer.cancel();
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

    // Plus device scan callback to find the nearest Connection groupOwner device who is hosting a group
    private ScanCallback scanCallbackPlusSearchOrRequestNetwork = new ScanCallback() {
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
            if (!plusController.containsIdGO(idGroupOwner)) {
                if (identifierApp.equals("connect") && mScanning) {
                    if (serviceType.equals(Task.ServiceEntry.serviceGroupOwner)) {
                        mScanning = false;
                        bluetoothLeScanner.stopScan(scanCallbackPlusSearchOrRequestNetwork);
                        countDownTimer.cancel();
                        plusController.connectToGroup(idGroupOwner.trim());
                    } else if (serviceType.equals(Task.ServiceEntry.serviceClientConnectedToGroupOwner)) {
                        mScanning = false;
                        bluetoothLeScanner.stopScan(scanCallbackPlusSearchOrRequestNetwork);
                        countDownTimer.cancel();
                        bluetoothAdvertiser.stopAdvertising();
                        bluetoothAdvertiser.setAdvertiseData(myId, Task.ServiceEntry.serviceRequestClientBecomeGroupOwner, idGroupOwner.trim());
                        bluetoothAdvertiser.startAdvertising();
                        setClientToRequestGroupId(idGroupOwner.trim());
                        initPlusScan(Task.ServiceEntry.serviceLookingForGroupOwnerWithSpecifiedId);
                    }
                }
            }
        }
    };

    // Plus device scan callback to find the nearest Connection groupOwner device who i ask to host the group
    private ScanCallback scanCallbackPlusLookingForGroupOwnerId = new ScanCallback() {
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
            if (!plusController.containsIdGO(idGroupOwner)) {
                if (identifierApp.equals("connect") && serviceType.equals(Task.ServiceEntry.serviceGroupOwner) && idGroupOwner.trim().equals(clientToRequestGroupId) && mScanning) {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(scanCallbackPlusLookingForGroupOwnerId);
                    countDownTimer.cancel();
                    bluetoothAdvertiser.stopAdvertising();
                    plusController.connectToGroup(idGroupOwner.trim());
                }
            }
        }
    };

}
