package com.example.lakuva;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationHelper {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final Activity activity;
    private final FusedLocationProviderClient fusedLocationClient;
    private final com.example.lakuva.LocationCallback locationCallback;
    private final LocationCallback locationUpdatesCallback;




    public LocationHelper(Activity activity, com.example.lakuva.LocationCallback locationCallback) {
        this.activity = activity;
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        this.locationCallback = locationCallback;

        // Define the LocationCallback for handling location updates
        this.locationUpdatesCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    locationCallback.onLocationRetrieved(
                            locationResult.getLastLocation().getLatitude(),
                            locationResult.getLastLocation().getLongitude());
                    locationCallback.onLocationResult(locationResult);
                } else {
                    Toast.makeText(activity, "Unable to retrieve location", Toast.LENGTH_SHORT).show();
                }
                // Stop location updates after receiving the location
                fusedLocationClient.removeLocationUpdates(this);
            }
        };
    }

    public void requestLocation() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    public void handlePermissionResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(activity, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
                if (location != null) {
                    locationCallback.onLocationRetrieved(location.getLatitude(), location.getLongitude());
                } else {
                    requestNewLocation();
                }
            }).addOnFailureListener(e -> Toast.makeText(activity, "Failed to retrieve location: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationUpdatesCallback, null);
        }
    }
}