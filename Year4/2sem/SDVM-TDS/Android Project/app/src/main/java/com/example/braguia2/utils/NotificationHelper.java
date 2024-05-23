package com.example.braguia2.utils;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.braguia2.R;
import com.example.braguia2.ui.activities.MainActivity;
import com.example.braguia2.ui.fragments.trail;

// NotificationHelper.java (utility class for notification handling)
public class NotificationHelper {

    private Context context;
    public NotificationHelper(Context context){
        this.context = context;
    }

    public void createNotification(String title, String message, String requestId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "geofence_channel")
                .setSmallIcon(R.drawable.location)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(getMainActivityPendingIntent2(requestId));

        // Create a notification manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Check if device is running Android Oreo or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create notification channel
            NotificationChannel channel = new NotificationChannel("geofence_channel", "Geofence Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Show the notification
        notificationManager.notify(100, builder.build());
    }


    private PendingIntent getMainActivityPendingIntent(String requestId){
        Intent intent = new Intent(context, trail.class);
        intent.setAction("ACTION_SHOW_TRAIL_FRAGMENT");
        intent.putExtra("trailId", requestId);
        return PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
    }

    private PendingIntent getMainActivityPendingIntent2(String id){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setAction("ACTION_SHOW_TRAIL_FRAGMENT");
        intent.putExtra("id", id);
        return PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);

    }
}
