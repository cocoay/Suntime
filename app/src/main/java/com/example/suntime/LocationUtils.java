package com.example.suntime;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.List;


public class LocationUtils implements LocationListener {

    static final String TAG = LocationUtils.class.getSimpleName();
    private final FragmentActivity activity;
    public final LocationHandler handle;
    public LocationManager manager;
    private final FragmentManager fragmentManager;
    private LocationFragment fragment;
    public boolean isAuthorization = false;


    public enum LocationStatus {
        unknown, notEnable, denied, granted
    }

    public interface LocationHandler {
        void success(Location location);

        void status(LocationStatus status);
    }

    public LocationUtils(@NonNull FragmentActivity activity, LocationHandler handle) {
        this.activity = activity;
        this.handle = handle;
        this.fragmentManager = activity.getSupportFragmentManager();
        setup();
    }

    public LocationUtils(@NonNull Fragment fragment, LocationHandler handle) {
        this.activity = fragment.getActivity();
        this.handle = handle;
        this.fragmentManager = fragment.getChildFragmentManager();
        setup();
    }

    private void setup() {
        this.manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.fragment = new LocationFragment(this);

        fragmentManager
                .beginTransaction()
                .add(fragment, TAG)
                .commitNow();
    }

    public boolean isLocationServicesEnabled() {
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean authorizationStatus() {
        return (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    public void requestAuthorization() {
        isAuthorization = true;
        if (isLocationServicesEnabled() == false) {
            handle.status(LocationStatus.notEnable);
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }
        handle.status(LocationUtils.LocationStatus.granted);
    }

    public void requestLocation() {
        isAuthorization = false;
        if (isLocationServicesEnabled() == false) {
            handle.status(LocationStatus.notEnable);
            return;
        }
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }
        List<String> providers = manager.getAllProviders();
        for (String provider : providers) {
            manager.requestLocationUpdates(provider, 10000, 1000, this);
        }
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


    // 定位LocationFragment，不展示
    public static class LocationFragment extends Fragment {

        private final LocationUtils locationManager;
        private static final int PERMISSIONS_REQUEST_CODE = 10086;

        public LocationFragment(LocationUtils locationManager) {
            this.locationManager = locationManager;
        }

        @TargetApi(Build.VERSION_CODES.M)
        void requestPermissions(@NonNull String[] permissions) {
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE);
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode != PERMISSIONS_REQUEST_CODE) return;

            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (locationManager.isAuthorization) {
                    locationManager.handle.status(LocationUtils.LocationStatus.granted);
                } else {
                    locationManager.requestLocation();
                }
            } else {
                // Permission denied.
                locationManager.handle.status(LocationUtils.LocationStatus.denied);
            }
        }
    }
}

