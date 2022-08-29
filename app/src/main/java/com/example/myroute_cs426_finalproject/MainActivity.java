package com.example.myroute_cs426_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myroute_cs426_finalproject.Adapter.RouteListAdapter;
import com.example.myroute_cs426_finalproject.Model.RouteModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

public class MainActivity extends AppCompatActivity implements LocationListener, RouteListAdapter.RouteModelListClickListener {

    private TextView tv_lat, tv_lon;
    private Switch sw_locationupdates;
    private static final int DEFAULT_UPDATE_INTERVAL = 4;
    private static final int FAST_UPDATE_INTERVAL = 1;
    private static final int PERMISSION_FINE_LOCATION = 99;

    private List<Location> locationList = new ArrayList<Location>();
    // Location request is a file for configuring the setting of FusedLocationProviderClient
    LocationRequest locationRequest;

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        sw_locationupdates = findViewById(R.id.sw_locationupdates);

        locationRequest = new LocationRequest();

        // how often does the default location check occur
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL * 1000);

        //how often does the location check occur when set to the most frequent update
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL * 1000);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationRequest.setSmallestDisplacement(2);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        // location will update when meet the interval
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // save the  location
                Location location = locationResult.getLastLocation();
                if(location != null){
                    UpdateUIValues(location);
                }
            }
        };

        sw_locationupdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw_locationupdates.isChecked()) {
                    startLocationUpdates();
                    Toast.makeText(MainActivity.this, "Start updating location ", Toast.LENGTH_SHORT).show();
                } else {
                    stopLocationUpdates();
                    Toast.makeText(MainActivity.this, "Stop updating location ", Toast.LENGTH_SHORT).show();

                }
            }
        });
        startLocationUpdates();
        stopLocationUpdates();
        //updateGPS();

        List<RouteModel> routeModelList = getRouteModelList();
        initRecyclerView(routeModelList);

    }   // onCreate method


    private List<RouteModel> getRouteModelList() {
//        InputStream is = getResources().openRawResource(R.raw.store);
        // write from file to list of store
        String jsonString = "";
        try {
            InputStream is = getResources().openRawResource(R.raw.routedata);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new Gson();
        RouteModel[] storeModels = gson.fromJson(jsonString, RouteModel[].class);
        List<RouteModel> routeModelList = Arrays.asList(storeModels);
        return routeModelList;
    }

    private void initRecyclerView(List<RouteModel> routeModelList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RouteListAdapter adapter = new RouteListAdapter(routeModelList, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLocationChanged(Location location) {
        // We received a location update on a separate thread!
        Log.w("onLocationChanged", String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getAltitude()));

        // You can verify which thread you're on by something like this:
        // Log.d("Which thread?", Thread.currentThread() == Looper.getMainLooper().getThread() ? "UI Thread" : "New thread");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //updateGPS();
                } else {
                    Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "Permission is denied", Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
            return;
        } else {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
            //updateGPS();
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }


    private void updateGPS() {
        // get permissions from user to track the location
        // get the current location from  the fused client
        // update UI

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // already permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // add values of location
                    if (location != null) {
                        // Logic to handle location object
                        UpdateUIValues(location);
                        Toast.makeText(MainActivity.this, "update successfully current location", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(MainActivity.this, "cannot get the current location", Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
                        }
                    }
                }

            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
            }
        }

    }

    private void UpdateUIValues(Location location) {

        if (location != null) {
            tv_lat.setText("Lat: " + String.valueOf(location.getLatitude()));
            tv_lon.setText("Long: " + String.valueOf(location.getLongitude()));
            Log.w("current location " , locationList.size() + ": "+ String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
            locationList.add(location);
        }

    }


    @Override
    public void onItemClick(RouteModel routeModel) {
        
    }
}