package com.example.suntime;

import androidx.appcompat.app.AppCompatActivity;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Location loc = new Location("suntime");
        loc.setLatitude(32.031971536589296);
        loc.setLongitude(118.7395957528125);
        SuntimeItem item = SuntimeManager.getSuntime(loc);
    }

    public void onClick(View view){
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        EditText latitudeInput = findViewById(R.id.latitude);
        EditText longitudeInput = findViewById(R.id.longitude);
        TextView textView = findViewById(R.id.textView);
        latitudeInput.clearFocus();
        longitudeInput.clearFocus();

        double latitude = Double.parseDouble(latitudeInput.getText().toString());
        double longitude = Double.parseDouble(longitudeInput.getText().toString());

        Location loc = new Location("suntime");
        loc.setLatitude(latitude);
        loc.setLongitude(longitude);
        String text = SuntimeManager.getSuntimeTest(loc);
        textView.setText(text);
    }

}