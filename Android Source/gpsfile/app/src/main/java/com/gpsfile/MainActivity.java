package com.gpsfile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private TextView locationText;
    private Handler handler = new Handler();
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationText = findViewById(R.id.location_text); 

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            requestLocation();
        }
    }

    private void requestLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    currentLocation = location;
                }

                @Override
                public void onProviderDisabled(@NonNull String provider) {}

                @Override
                public void onProviderEnabled(@NonNull String provider) {}

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}
            });

            startLocationUpdates();
        } catch (SecurityException e) {
            Toast.makeText(this, "Permission denied for location", Toast.LENGTH_SHORT).show();
        }
    }

    private void startLocationUpdates() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentLocation != null) {
                    showOnScreen(currentLocation); 
                    writeToFile(currentLocation); 
                } else {
                    Toast.makeText(MainActivity.this, "Waiting for location...", Toast.LENGTH_SHORT).show();
                }
                handler.postDelayed(this, 200); 
            }
        }, 200); 
    }

    private void showOnScreen(Location location) {
        String text = "Lat: " + location.getLatitude() + "\nLon: " + location.getLongitude();
        locationText.setText(text); 
    }

    private void writeToFile(Location location) {
        String timestamp = getCurrentTimestamp();
        String text = timestamp + " - Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude() + "\n";

        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, "location.txt");

        try {

            if (!file.exists()) {
                file.createNewFile(); 
            }


            try (FileWriter writer = new FileWriter(file, true)) {
                writer.append(text);
                Toast.makeText(this, "Location saved to Downloads", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            Toast.makeText(this, "Error writing file", Toast.LENGTH_SHORT).show();
            Log.e("LocationLogger", "File write failed", e);
        }
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocation(); 
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
