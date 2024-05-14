package com.example.braguia2.utils;

import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.braguia2.model.Trails.Ponto;
import com.example.braguia2.viewmodel.TrailsViewModel;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

public class GeofencingClass {
    private Context context;
    private List<Geofence> geofenceList = new ArrayList<>();
    ;
    private GeofencingClient geofencingClient;

    public GeofencingClass(Context context) {
        Log.d(TAG,"eheheheheh");
        this.context = context;
        this.geofencingClient = LocationServices.getGeofencingClient(this.context);
    }

    public void setUpGeofences() {
        TrailsViewModel trailsViewModel = new ViewModelProvider((ViewModelStoreOwner) this.context).get(TrailsViewModel.class);

        trailsViewModel.getAllPontos().observe((LifecycleOwner) this.context, points -> {
            if (points != null) {
                for (Ponto point : points) {
                    // Create a geofence object for each point
                    createGeofence(point);
                }
                addGeofencesToClient();
            }
        });
    }

    private void addGeofencesToClient() {
        // Check if geofenceList is not empty and geofencingClient is initialized
        Log.d("TOU AQUI","TOU AQUI");
        Log.d("LIST CHECK", geofenceList.get(0).getRequestId());
        if (!geofenceList.isEmpty() && geofencingClient != null) {
            Log.d("IM IN","IM IN");
            // Create the geofencing request
            GeofencingRequest geofencingRequest = getGeofencingRequest();
            Log.d("IM IN","IM IN");
            // Add geofences to the geofencing client
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.d("TENHO PERMS","TENHO PERMS");
                Log.d("CLIENT CHECK", geofencingClient.toString());
                geofencingClient.addGeofences(geofencingRequest, getGeofencePendingIntent())
                        .addOnSuccessListener(aVoid -> {
                            // Geofences added successfully
                            Log.d("GeofencingClass", "Geofences added successfully");
                        })
                        .addOnFailureListener(e -> {
                            // Failed to add geofences
                            Log.e("GeofencingClass", "Failed to add geofences: " + e.getMessage());
                        });
            }
        }
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_MUTABLE);
    }






    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }


    private void createGeofence(Ponto point) {
        geofenceList.add(new Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this geofence.
                .setRequestId(point.getId())
                .setCircularRegion(
                        point.getPinLat(),
                        point.getPinLng(),
                        1500
                )
                .setLoiteringDelay(10000)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());
    }

}

