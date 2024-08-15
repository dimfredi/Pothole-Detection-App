package com.example.lakuva;

// Import necessary classes for working with SQLite database, Android utilities, and Google Maps LatLng
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;

public class LocationDatabaseHelper extends SQLiteOpenHelper {

    // Constants for the database name, version, and table/column names
    private static final String DATABASE_NAME = "locations.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "LocationDBHelper";

    // Table and columns for storing locations
    private static final String TABLE_LOCATIONS = "locations";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";

    // Constructor to initialize the database helper
    public LocationDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create the locations table
        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_LATITUDE + " REAL,"
                + COLUMN_LONGITUDE + " REAL" + ")";
        // Execute the SQL statement
        db.execSQL(CREATE_LOCATIONS_TABLE);
    }

    // Called when the database is upgraded (version number is incremented)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the existing locations table and recreate it
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    // Method to add a new location to the database
    public void addLocation(double latitude, double longitude) {
        // Get a writable database instance
        SQLiteDatabase db = this.getWritableDatabase();

        // Prepare the data to be inserted
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_LONGITUDE, longitude);

        // Insert the data into the table and log the result
        long result = db.insert(TABLE_LOCATIONS, null, values);
        db.close();
        Log.d(TAG, "Location added: " + latitude + ", " + longitude + " (ID: " + result + ")");
    }

    // Method to retrieve all stored locations from the database
    public List<LatLng> getAllLocations() {
        List<LatLng> locations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATIONS; // SQL query to select all rows

        // Get a readable database instance
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            // Execute the query and get a cursor for the results
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                int latitudeIndex = cursor.getColumnIndex(COLUMN_LATITUDE);
                int longitudeIndex = cursor.getColumnIndex(COLUMN_LONGITUDE);
                do {
                    // Retrieve latitude and longitude from the current row
                    double latitude = cursor.getDouble(latitudeIndex);
                    double longitude = cursor.getDouble(longitudeIndex);
                    // Add the location to the list
                    locations.add(new LatLng(latitude, longitude));
                    Log.d(TAG, "Location retrieved: " + latitude + ", " + longitude);
                } while (cursor.moveToNext()); // Move to the next row in the result set
            }
        } finally {
            // Always close the cursor and database when done
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        Log.d(TAG, "Total locations retrieved: " + locations.size());
        return locations;
    }

    // Method to remove a specific location from the database
    public void removeLocation(double latitude, double longitude) {
        // Get a writable database instance
        SQLiteDatabase db = this.getWritableDatabase();

        // Define the WHERE clause and the arguments for the deletion
        String whereClause = COLUMN_LATITUDE + "=? AND " + COLUMN_LONGITUDE + "=?";
        String[] whereArgs = new String[] { String.valueOf(latitude), String.valueOf(longitude) };

        // Delete the row(s) matching the criteria and log the result
        int result = db.delete(TABLE_LOCATIONS, whereClause, whereArgs);
        db.close();
        Log.d(TAG, "Location removed: " + latitude + ", " + longitude + " (Rows affected: " + result + ")");
    }
}