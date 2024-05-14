package com.example.braguia2.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.braguia2.R;
import com.example.braguia2.utils.GeofenceBroadcastReceiver;
import com.example.braguia2.utils.GeofencingClass;
import com.example.braguia2.utils.LocationForegroundService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private GeofencingClass geofencingClass;
    private GeofenceBroadcastReceiver geofenceReceiver;
    private PendingIntent geofencePendingIntent;
    private int flag = -1;
    private int flagg = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Intent serviceIntent = new Intent(this, LocationForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        navigateToTrailFragmentIfNeeded(getIntent());
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.host_frag);
        NavigationUI.setupWithNavController(bottomNavigation, navController);

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check and request the ACCESS_FINE_LOCATION permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (flag == -1){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        100);
                flag = 0;
            }

        } else {
            // Permission already granted, set up geofences
            //geofencingClass.setUpGeofences();
            Intent serviceIntent = new Intent(this, LocationForegroundService.class);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
        && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            geofencingClass = new GeofencingClass(this);
            geofencingClass.setUpGeofences();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED && flagg == -1) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 304);
        }
        flagg = 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, set up geofences
                //geofencingClass.setUpGeofences();
                Intent serviceIntent = new Intent(this, LocationForegroundService.class);
                ContextCompat.startForegroundService(this, serviceIntent);
            } else {
                Toast.makeText(this, "Impossível encontrar pontos próximos", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 301){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the image
                // bindImage(conteudo, user);
            } else {
                // Permission denied, handle the situation accordingly (e.g., show a message)
                Toast.makeText(this, "Permission denied. Unable to load image from Storage.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 302){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the image
                // bindImage(conteudo, user);
            } else {
                // Permission denied, handle the situation accordingly (e.g., show a message)
                Toast.makeText(this, "Permission denied. Unable to load video from Storage.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 303){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the image
                // bindImage(conteudo, user);
            } else {
                // Permission denied, handle the situation accordingly (e.g., show a message)
                Toast.makeText(this, "Permission denied. Unable to load audio from Storage.", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == 304){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, load the image
                // bindImage(conteudo, user);
            } else {
                // Permission denied, handle the situation accordingly (e.g., show a message)
                Toast.makeText(this, "Permission denied. Unable to load media from Storage.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void navigateToTrailFragmentIfNeeded(@NonNull Intent intent){
        if(intent.getAction()!=null && intent.getAction().equals("ACTION_SHOW_TRAIL_FRAGMENT")){
            String id = intent.getStringExtra("id");
            Log.d("NOTIFICATION CLICK", id);
            NavController navController = Navigation.findNavController(this,R.id.host_frag);
            if (Integer.parseInt(id) != -1) {
                // If ID is valid, navigate to pontoFragment with the ID
                Bundle args = new Bundle();
                args.putInt("id", Integer.parseInt(id));
                navController.navigate(R.id.action_global_pontoFragment, args);
            } else {
                // If ID is not valid, just navigate to pontoFragment without passing the ID
                navController.navigate(R.id.action_global_pontoFragment);
            }
        }
    }


    @Override
    public void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        navigateToTrailFragmentIfNeeded(intent);
    }



    @Override
    protected void onPause() {
        super.onPause();
    }
}