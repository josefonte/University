package com.example.braguia2.utils;

import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.braguia2.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class LocationForegroundService extends Service {
    private FusedLocationProviderClient fusedLocationClient;
    private Context context;

    public LocationForegroundService(){
        Log.d(TAG,"akkkkkkkkk");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //startForeground(1, createNotification());
        Log.d("USER LOCATION","OI");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel("geofence channel", "Android Services", NotificationManager.IMPORTANCE_LOW);
            channel.setDescription("Tracking Location");
            notificationManager.createNotificationChannel(channel);

            startForeground(191, createNotificationLocating());
            this.context = getApplicationContext();
            requestLocationUpdates();
        }
        return START_STICKY;
    }

    private Notification createNotificationLocating() {
        // Create and return a notification for the foreground service
        // You can customize this notification based on your requirements
        return new NotificationCompat.Builder(this, "geofence channel")
                .setContentTitle("Foreground Service")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.location)
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Log.d("USER LOCATION", Double.toString(location.getLatitude()));
                Log.d("USER LOCATION", Double.toString(location.getLongitude()));
            }
        }
    };

    private void requestLocationUpdates() {
        Log.d("USER LOCATION", "OIIIII");
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        } else {
            // Permission is not granted, request it from the user
            ActivityCompat.requestPermissions((Activity)context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    201);
        }


    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

