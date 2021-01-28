package com.example.suntime;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class LocationUtlis implements LocationListener {

    private Activity activity;
    public final LocationManager manager;
    public final LocationHandler handle;
    public static final int REQUEST_CODE = 10086;


    public enum LocationError {
        unknown, notEnable, denied
    }

    public interface LocationHandler {
        void success(Location location);
        void failure(LocationError error);
    }

    public LocationUtlis(@NonNull Activity activity, LocationHandler handle) {
        this.activity = activity;
        this.handle = handle;
        this.manager = (LocationManager)activity.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isLocationServicesEnabled() {
        boolean gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return gps;
    }

    public boolean authorizationStatus() {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestLocation() {
        if (isLocationServicesEnabled() == false) {
            handle.failure(LocationError.notEnable);
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE);
            return;
        }
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1000, this);
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        handle.success(location);
        manager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
