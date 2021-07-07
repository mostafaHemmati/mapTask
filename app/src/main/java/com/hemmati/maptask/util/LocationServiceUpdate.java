package com.hemmati.maptask.util;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;
import com.hemmati.maptask.MainActivity;
import com.hemmati.maptask.R;

import static com.hemmati.maptask.util.Constants.ACTION_BROADCAST;
import static com.hemmati.maptask.util.Constants.EXTRA_EXIT;
import static com.hemmati.maptask.util.Constants.EXTRA_LOCATION;
import static com.hemmati.maptask.util.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.hemmati.maptask.util.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;

public class LocationServiceUpdate extends Service {


    private static final String CHANNEL_ID = "Location Channel";


    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                location = locationResult.getLastLocation();
                onNewLocation(locationResult.getLastLocation());
            }
        }
    };
    private Location location;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not yet implemented!");
    }

    private void startLocationService() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder;

        Intent resultIntentService = new Intent(this, LocationServiceUpdate.class);
        resultIntentService.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);

        PendingIntent servicePendingIntent = PendingIntent.getService(this, 0, resultIntentService,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Intent resultIntentActivity = new Intent(this, MainActivity.class);
        resultIntentActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent activityPendingIntent = PendingIntent.getActivity(this, 0,
                resultIntentActivity, 0);


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "LocationChannel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Location channel description");
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder
                .setContentTitle("مسیریاب")
                .addAction(android.R.drawable.ic_media_play, "بازگشت به اپلیکیشن",
                        activityPendingIntent)
                .addAction(android.R.drawable.ic_media_pause, " خروج ",
                        servicePendingIntent)
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_orang_marker)
                .setWhen(System.currentTimeMillis()).build();

        initLocationRequest(builder);
    }

    private void initLocationRequest(NotificationCompat.Builder builder) {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        FusedLocationProviderClient locationServices = LocationServices.getFusedLocationProviderClient(this);
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        locationServices.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationService();
    }

    private void stopLocationService() {
        exitApp();
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    private void exitApp() {
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        intent.putExtra(EXTRA_EXIT, true);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE))
                    startLocationService();
                else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE))
                    stopLocationService();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void onNewLocation(Location location) {

        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        intent.putExtra(EXTRA_EXIT, false);

        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

}
