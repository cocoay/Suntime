package com.example.suntime;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import androidx.annotation.NonNull;
import java.util.HashMap;

public class WifiUtils {

    public static HashMap<String, String> getCurrentWiFiInfo(@NonNull Activity activity) {
        WifiManager wifiManager = (WifiManager)activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID().replace("\"", "");
        String bssid = info.getBSSID();
        if (!ssid.isEmpty() && !bssid.isEmpty()) {
            HashMap map = new HashMap();
            map.put("ssid", ssid);
            map.put("bssid", bssid);
            return map;
        }
        return null;
    }

    public static String getCurrentWiFiInfoString(@NonNull Activity activity) {
        HashMap<String, String> map = getCurrentWiFiInfo(activity);
        if (map != null) {
            return  "ssid: " + map.get("ssid")  +"\nbssid: " + map.get("bssid");
        }
        return null;
    }
}
