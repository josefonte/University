package com.example.braguia2.viewmodel;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.google.android.exoplayer2.mediacodec.MediaCodecInfo.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.braguia2.R;
import com.example.braguia2.ui.activities.MainActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Polyline;

public class LocationService extends Service {

    private static final String CHANNEL_ID = "LocationForegroundService";

    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private LocationManager mLocationManager;
    private Boolean isFirtRun = true;

    public static MutableLiveData<Boolean> isTracking = new MutableLiveData<>();
    public static MutableLiveData<Polyline> pathpoints = new MutableLiveData<>();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            String action = intent.getAction();
            switch (action) {
                case "ACTION_START_OR_RESUME_SERVICE":
                    Log.d(TAG, "onStartCommand: STARTED THE SERVICE ");
                    if (isFirtRun) {
                        startForegroundService();
                        requestLocationUpdates();
                        isFirtRun = false;
                    } else {
                        Log.d(TAG, "onStartCommand: RESUMING THE SERVICE");
                    }
                    break;
                case "ACTION_PAUSE_SERVICE":
                    Log.d(TAG, "onStartCommand: PAUSED THE SERVICE");
                    break;
                case "ACTION_STOP_SERVICE":
                    Log.d(TAG, "onStartCommand: STOPED THE SERVICE");
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForegroundService() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("BraGuia")
                .setContentText("Trilho ehehe")
                .setContentIntent(getMainActivityPendingIntent());


        startForeground(1, notificationBuilder.build());

/*
        // Before starting the service as foreground check that the app has the appropriate runtime permissions.
        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE);

        if(locationPermission == PackageManager.PERMISSION_DENIED){
            // Without location permissions the service cannot run in the foreground.
            // TODO: Dar fix a isto de forma a que apareca a dizer que tem que permitir o uso
            //      da localização
            stopSelf();
            return;
        }
        try {
            Notification notification =
                    new NotificationCompat.Builder(this, "CHANNEL_ID")
                            // Create the notification to display while the service
                            // is running
                            .build();
            int type = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                type = ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;
            }
            ServiceCompat.startForeground(
                    /* service =  this,
                    /* id = 100, // Cannot be 0
                    /* notification =  notification,
                    /* foregroundServiceType =  type
            );
        } catch (Exception e) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    e instanceof ForegroundServiceStartNotAllowedException
            ) {
                // App not in a valid state to start foreground service
                // (e.g started from bg)
            }
            // ...
        }
*/
    }

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); // Update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    private PendingIntent getMainActivityPendingIntent(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.setAction("ACTION_SHOW_TRAIL_FRAGMENT");
        return PendingIntent.getActivity(this, 0, intent, FLAG_UPDATE_CURRENT);

    }

    private void createNotificationChannel(NotificationManager notificationManager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(serviceChannel);
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
