package com.example.suntime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText latitudeInput;
    private EditText longitudeInput;
    private TextView textView;
    private LocationUtils locationManager;
    private boolean isWifi = false;
    private KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.latitudeInput = findViewById(R.id.latitude);
        this.longitudeInput = findViewById(R.id.longitude);
        this.textView = findViewById(R.id.textView);

        this.locationManager = new LocationUtils(this, new LocationUtils.LocationHandler() {
            @Override
            public void success(Location location) {
                if (isWifi) {
                    setupWifi();
                } else  {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    latitudeInput.setText("" + latitude);
                    longitudeInput.setText("" + longitude);
                    String text = SuntimeManager.getSuntimeTest(location);
                    textView.setText(text);
                }
            }

            @Override
            public void failure(LocationUtils.LocationError error) {
                switch (error) {
                    case notEnable:
                        System.out.println("定位服务不可用");
                        break;
                    case denied:
                        System.out.println("无权限");
                        break;
                    default :
                        break;
                }
            }
        });
    }

    public void calculateClick(View view){
        closeInputView(view);
        double latitude = Double.parseDouble(latitudeInput.getText().toString());
        double longitude = Double.parseDouble(longitudeInput.getText().toString());

        Location loc = new Location("suntime");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        String text = SuntimeManager.getSuntimeTest(loc);
        textView.setText(text);
    }

    public void locationClick(View view){
        closeInputView(view);
        isWifi = false;

        locationManager.requestLocation();
    }

    public void wifiClick(View view){
        closeInputView(view);
        isWifi = true;

        locationManager.requestLocation();
    }

    public void networkClick(View view){
        closeInputView(view);

        String latitude = latitudeInput.getText().toString();
        String longitude = longitudeInput.getText().toString();

        hud = KProgressHUD.create(MainActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .show();

        Call<ResultItem> task = NetworkUtils.shared().apiService.getSuntime(latitude, longitude);
        task.enqueue(new Callback<ResultItem>() {
            @Override
            public void onResponse(Call<ResultItem> call, Response<ResultItem> response) {
                ResultItem item = response.body();
                hud.dismiss();
                textView.setText(item.toString());
            }

            @Override
            public void onFailure(Call<ResultItem> call, Throwable t) {
                hud.dismiss();
            }
        });
    }

    private void setupWifi() {
        String text = WifiUtils.getCurrentWiFiInfoString(this);
        textView.setText(text);
    }

    private void closeInputView(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LocationUtils.REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocation();
            } else {
                // Permission denied.
                locationManager.handle.failure(LocationUtils.LocationError.denied);
            }
        }
    }
}