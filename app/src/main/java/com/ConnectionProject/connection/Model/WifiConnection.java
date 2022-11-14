package com.ConnectionProject.connection.Model;

import android.annotation.SuppressLint;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

import java.util.List;

public class WifiConnection {

    private WifiConfiguration conf;

    public WifiConnection(String SSID, String password, WifiManager wifiManager) {
        conf = new WifiConfiguration();
        conf.SSID =  "\"" + SSID + "\"";
        conf.preSharedKey =  "\"" + password + "\"";
        wifiManager.addNetwork(conf);
        @SuppressLint("MissingPermission") List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + SSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
    }
}
