package com.example.braguia2.utils;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

// GeofenceBroadcastReceiver.java
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private Context context;

    public GeofenceBroadcastReceiver() {
        //this.context = context;
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e("geofencing", errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();

        Geofence geofence = geofencingEvent.getTriggeringGeofences().get(0);


        String requestId = geofence.getRequestId();



        switch (geofenceTransition) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d("GEOFENCING BROADCAST","oi");
                showNotification(this.context, "PONTO DE INTERESSE BASTANTE PERTO!!!", requestId);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d("GEOFENCING BROADCAST","oi");
                showNotification(this.context, "PONTO DE INTERESSE BASTANTE PERTO!!!", requestId);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d("GEOFENCING BROADCAST","oi");
                showNotification(this.context, "PONTO DE INTERESSE BASTANTE PERTO!!!", requestId);
                break;
            default:
                Log.e("geofencing", "Unknown transition type: " + geofenceTransition);
        }
    }

    private void showNotification(Context context, String message, String requestId) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        notificationHelper.createNotification("Geofence Notification", message, requestId);
    }
}
