package com.ConnectionProject.connection.Listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;

import com.ConnectionProject.connection.Controller.AutoClicker;
import com.ConnectionProject.connection.Controller.ConnectionController;

public class WiFiDirectBroadcastListener extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    WifiP2pInfo wifiP2PInfo = new WifiP2pInfo();
    WifiP2pManager.PeerListListener peerListListener;
    WifiP2pManager.ConnectionInfoListener connectionInfoListener;
    AutoClicker autoClicker;
    ConnectionController connectionController;

    public WiFiDirectBroadcastListener(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, WifiP2pManager.PeerListListener peerListListener, WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
        this.mManager = mManager;
        this.mChannel = mChannel;
        this.peerListListener = peerListListener;
        this.connectionInfoListener = connectionInfoListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            if (mManager != null) {
                mManager.requestConnectionInfo(mChannel,
                        new WifiP2pManager.ConnectionInfoListener() {

                            @Override
                            public void onConnectionInfoAvailable(
                                    WifiP2pInfo info) {
                                if (info != null) {
                                    if (info.groupFormed && info.isGroupOwner) {

                                       /* new CountDownTimer(5000, 1000) {

                                            public void onTick(long millisUntilFinished) {
                                            }

                                            public void onFinish() {
                                                autoClicker=AutoClicker.getInstance();
                                                autoClicker.clicker();
                                            }
                                        }.start();
*/

                                       // mManager.requestPeers(mChannel,peerListListener);

                                    }
                                }
                            }
                        });
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }
            //connectionController.clientList();
            /*NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, connectionInfoListener);
            } else {
                String ConnectionStatus="Device disconnected";
            }*/
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }
}