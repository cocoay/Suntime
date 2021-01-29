package com.example.suntime;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.HashMap;
import java.util.List;

public class WifiUtils {

    public static HashMap<String, String> getCurrentWiFiInfo(@NonNull Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID().replace("\"", "");
        String bssid = wifiInfo.getBSSID();

        if (ssid.isEmpty() || bssid.isEmpty()) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            int networkId = wifiInfo.getNetworkId();
            List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration wifiConfiguration:configuredNetworks){
                if (wifiConfiguration.networkId == networkId){
                    ssid = wifiConfiguration.SSID;
                    bssid = wifiConfiguration.BSSID;
                    break;
                }
            }
        }

        HashMap map = new HashMap();
        map.put("ssid", ssid);
        map.put("bssid", bssid);
        return map;
    }

    public static String getCurrentWiFiInfoString(@NonNull Context context) {
        HashMap<String, String> map = getCurrentWiFiInfo(context);
        if (map != null) {
            return  "ssid: " + map.get("ssid")  +"\nbssid: " + map.get("bssid");
        }
        return null;
    }
}
