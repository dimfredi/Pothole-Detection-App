package com.example.lakuva;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
public class MainActivity extends AppCompatActivity implements  LocationCallback.FullScreenInterface {

    private static final int REQUEST_CODE = 22;
    Button photo_button;
    Button location_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableFullScreenMode(getWindow());
        disableNotch(getWindow());

        photo_button = findViewById(R.id.photo_button);
        location_button = findViewById(R.id.location_button);


        photo_button.setOnClickListener(v -> {
            //create an intent
            Intent cameraIntent = new Intent(MainActivity.this, CameraCaptureActivity.class);
            startActivityForResult(cameraIntent, REQUEST_CODE);
        });

        location_button.setOnClickListener(v -> {
            //create an intent
            Intent locationIntent = new Intent(MainActivity.this, MapsActivity.class);
            startActivityForResult(locationIntent, REQUEST_CODE);
        });

    }
}