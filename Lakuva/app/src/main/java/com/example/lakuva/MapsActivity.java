package com.example.lakuva;
// Import necessary Android and Google Maps packages
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationCallback,LocationCallback.FullScreenInterface {

    // Declare variables for GoogleMap, location helper, database helper, and UI elements
    private GoogleMap mMap;
    private LocationHelper locationHelper;
    private LocationDatabaseHelper locationDatabaseHelper;
    private Button buttonRemove;
    private LatLng selectedLocation;

    // Override the onCreate method to set up the activity
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps); // Set the layout for this activity


        enableFullScreenMode(getWindow());
        disableNotch(getWindow());
        // Initialize UI elements
        Button buttonback = findViewById(R.id.buttonback);
        buttonRemove = findViewById(R.id.buttonRemove);

        // Set up the map fragment and request the map to be loaded asynchronously
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize the database helper for managing location data
        locationDatabaseHelper = new LocationDatabaseHelper(this);

        // Set up the back button to finish the activity when clicked
        buttonback.setOnClickListener(v -> finish());

        // Set up the remove button to delete the selected location from the database
        buttonRemove.setOnClickListener(v -> {
            if (selectedLocation != null) {
                // Remove the selected location from the database
                locationDatabaseHelper.removeLocation(selectedLocation.latitude, selectedLocation.longitude);
                mMap.clear(); // Clear all markers on the map

                // Retrieve and display all saved locations as markers
                List<LatLng> savedLocations = locationDatabaseHelper.getAllLocations();
                for (LatLng location : savedLocations) {
                    mMap.addMarker(new MarkerOptions().position(location).title("Lakuva"));
                }

                // Show a confirmation message and reset the selected location
                Toast.makeText(MapsActivity.this, "Location removed", Toast.LENGTH_SHORT).show();
                selectedLocation = null;
                buttonRemove.setEnabled(false); // Disable the remove button
            } else {
                // Show a message if no location is selected
                Toast.makeText(MapsActivity.this, "No location selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Called when the map is ready to be used
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap; // Store the reference to the GoogleMap object

        // Initialize location helper and request current location
        locationHelper = new LocationHelper(this, this);
        locationHelper.requestLocation();

        // Display all saved locations as markers on the map
        List<LatLng> savedLocations = locationDatabaseHelper.getAllLocations();
        for (LatLng location : savedLocations) {
            mMap.addMarker(new MarkerOptions().position(location).title("Lakuva"));
        }

        // Set up a marker click listener to handle marker selection
        mMap.setOnMarkerClickListener(marker -> {
            selectedLocation = marker.getPosition();
            marker.showInfoWindow();
            buttonRemove.setEnabled(true); // Enable the remove button when a marker is selected
            return true;
        });

        // Set up a map click listener to handle clicks on the map
        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = null;
            buttonRemove.setEnabled(false); // Disable the remove button when clicking on the map
        });
    }

    // Called when the location is retrieved from the location helper
    @Override
    public void onLocationRetrieved(double latitude, double longitude) {
        LatLng currentLocation = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15)); // Move the camera to the current location
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("I am here!")); // Add a marker for the current location
    }

    // Override this method to handle location results
    @Override
    public void onLocationResult(LocationResult locationResult) {
        // Implement location result handling if needed
    }

    // Handle the result of the permission request for location access
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        locationHelper.handlePermissionResult(requestCode, grantResults); // Forward the results to the location helper
    }
}