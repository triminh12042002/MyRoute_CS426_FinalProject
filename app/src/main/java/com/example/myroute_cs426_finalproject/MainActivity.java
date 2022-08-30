package com.example.myroute_cs426_finalproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.gms.location.Priority;
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
    private EditText nameET, infoET;
    private static final int DEFAULT_UPDATE_INTERVAL = 4;
    private static final int FAST_UPDATE_INTERVAL = 1;
    private static final int PERMISSION_FINE_LOCATION = 99;

    private List<Location> locationList = new ArrayList<Location>();
    // Location request is a file for configuring the setting of FusedLocationProviderClient
    LocationRequest locationRequest;

    // Google's API for location services
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private boolean requestingLocationUpdates = false;
    private boolean requestTracking = false;
    private Button buttonStartTracking;
    private Button buttonStopTracking;
    private List<RouteModel> routeModelList;
    private RouteListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        tv_lat = findViewById(R.id.tv_lat);
        tv_lon = findViewById(R.id.tv_lon);
        sw_locationupdates = findViewById(R.id.sw_locationupdates);
        buttonStartTracking = findViewById(R.id.buttonStartTracking);
        buttonStopTracking = findViewById(R.id.buttonStopTracking);
        buttonStopTracking.setEnabled(false);
        nameET = findViewById(R.id.nameET);
        infoET = findViewById(R.id.infoET);

        locationRequest = LocationRequest.create();

        // how often does the default location check occur
        locationRequest.setInterval(DEFAULT_UPDATE_INTERVAL * 1000);

        //how often does the location check occur when set to the most frequent update
        locationRequest.setFastestInterval(FAST_UPDATE_INTERVAL * 1000);

        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);

        locationRequest.setSmallestDisplacement(2);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        buttonStartTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                requestTracking = true;
                startLocationUpdates();
                //routeModel.setLocationList(locationList);
                //Intent intent = new Intent(MainActivity.this, MapsFragment.class);
                //.putExtra("RouteModel",routeModel);
                //startActivity(intent);
                buttonStartTracking.setEnabled(false);
                buttonStopTracking.setEnabled(true);
                sw_locationupdates.setChecked(true);
                nameET.setText("");
                infoET.setText("");
                nameET.setEnabled(true);
                infoET.setEnabled(true);

            }
        });
        buttonStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestTracking = false;
                stopLocationUpdates();
                buttonStartTracking.setEnabled(true);
                buttonStopTracking.setEnabled(false);
                nameET.setEnabled(false);
                infoET.setEnabled(false);
                addNewRouteModel();
            }
        });
        // location will update when meet the interval
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // save the  location
                Location location = locationResult.getLastLocation();
                if (location != null) {
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
        //startLocationUpdates();
        //stopLocationUpdates();
        //updateGPS();

        routeModelList = new ArrayList<>(getRouteModelList());
        if(routeModelList != null)
            initRecyclerView(routeModelList);
        else{
            Toast.makeText(this, "routeModelList is empty", Toast.LENGTH_SHORT).show();
        }

    }   // onCreate method

    private void addNewRouteModel() {
        RouteModel routeModel = new RouteModel();
        routeModel.setLocationList(locationList);
        int size = locationList.size();
        String[] latlngs = new String[size];
        for(int i = 0; i < size; ++i){
            latlngs[i] = locationList.get(i).getLatitude() + "," + locationList.get(i).getLongitude();
        }
        routeModel.setName(nameET.getText().toString());
        routeModel.setInfo(infoET.getText().toString());
        routeModel.setLatlngs(latlngs);
        Log.w("routeModelList", String.valueOf(routeModelList.size()));

        if(!routeModelList.isEmpty()) {
            routeModelList.add(routeModel);
        }

        adapter.updateData(routeModelList);
    }

    private void WriteRouteModelListDataToFile(List<RouteModel> routeModelList){

    }

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
        Location l;

        Gson gson = new Gson();
        RouteModel[] routeModels = gson.fromJson(jsonString, RouteModel[].class);
        List<RouteModel> routeModelList = Arrays.asList(routeModels);
        return routeModelList;
    }

    private void initRecyclerView(List<RouteModel> routeModelList) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RouteListAdapter(routeModelList, this);
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
        requestingLocationUpdates = true;
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
        requestingLocationUpdates = false;
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
            if(requestTracking){
                locationList.add(location);
            }
            Log.w("current location " , locationList.size() + ": "+ String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()));
        }
    }


    @Override
    public void onItemClick(RouteModel routeModel) {
        List<Location> locationList = new ArrayList<>();
        String[] latlngs = routeModel.getLatlngs();
        String[] latlong;
        Location location;
        for(String latlng : latlngs){
            latlong =  latlng.split(",");
            double latitude = Double.parseDouble(latlong[0]);
            double longitude = Double.parseDouble(latlong[1]);
            location = new Location("");
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            locationList.add(location);
        }

        routeModel.setLocationList(locationList);
        Intent intent = new Intent(MainActivity.this, PolyActivity.class);
        intent.putExtra("RouteModel",routeModel);
        startActivity(intent);
    }
}
