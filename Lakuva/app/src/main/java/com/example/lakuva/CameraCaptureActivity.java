package com.example.lakuva;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.LocationResult;

public class CameraCaptureActivity extends AppCompatActivity implements LocationCallback,LocationCallback.FullScreenInterface {
    // Request codes for camera and location permissions
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;


    private ImageView CameraView;
    private TextView locationTextView;

    private Button Submit;


    // Helpers for handling location and database operations
    private LocationHelper locationHelper;
    private LocationDatabaseHelper locationDatabaseHelper;

    // Variables to store current latitude and longitude
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_capture);

        enableFullScreenMode(getWindow());
        disableNotch(getWindow());
        // Initialization of UI elements
        CameraView = findViewById(R.id.CameraView);
        Button camerabtn = findViewById(R.id.camerabtn);
        Submit = findViewById(R.id.Submit);
        locationTextView = findViewById(R.id.locationTextView);
        ImageView back_button = findViewById(R.id.back_button);

        // Initialization of helper objects
        locationHelper = new LocationHelper(this, this);
        locationDatabaseHelper = new LocationDatabaseHelper(this);

        // Set click listener for back button to finish the activity
        back_button.setOnClickListener(v -> finish());

        // Set click listener for camera button to handle camera permission and location request
        camerabtn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request camera permission if not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
            } else {
                // Request location if camera permission is already granted
                locationHelper.requestLocation();
            }
        });


        // submit button (initially hidden)
        Submit.setOnClickListener(v -> {
            // Save the location in the SQLite database
            locationDatabaseHelper.addLocation(currentLatitude, currentLongitude);
            Log.d(TAG, "Location saved: " + currentLatitude + ", " + currentLongitude);
            Toast.makeText(this, "Location uploaded", Toast.LENGTH_SHORT).show();
            // Hide Submit button after upload
            Submit.setVisibility(Button.GONE);
        });
    }

    // Method to capture a photo using the camera
    private void capturePhoto() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Start camera activity to capture image
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            // Show message if no camera app is available
            Toast.makeText(this, "No camera app available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                // Retrieve the captured photo as a Bitmap
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                // Display the photo in the ImageView
                CameraView.setImageBitmap(photo);
                // Format and display the current location
                @SuppressLint("DefaultLocale") String locationText = String.format("Location: %.4f, %.4f", currentLatitude, currentLongitude);
                locationTextView.setText(locationText);

                // Show Submit button after image is captured
                Submit.setVisibility(Button.VISIBLE);
            }
        } else {
            // Show message if photo capture is canceled
            Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationRetrieved(double latitude, double longitude) {
        // Store the retrieved latitude and longitude
        currentLatitude = latitude;
        currentLongitude = longitude;
        // Capture a photo after the location is retrieved
        capturePhoto();
    }

    @Override
    public void onLocationResult(LocationResult locationResult) {
        // This method can be used to handle location results if needed
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // Handle the result of camera permission request
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                capturePhoto();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Handle the result of location permission request
            locationHelper.handlePermissionResult(requestCode, grantResults);
        }
    }
}
