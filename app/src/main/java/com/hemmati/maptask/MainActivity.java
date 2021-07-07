package com.hemmati.maptask;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.hemmati.maptask.base.BaseActivity;
import com.hemmati.maptask.util.Constants;
import com.hemmati.maptask.util.LocationServiceUpdate;


public class MainActivity extends BaseActivity {


    @Override
    protected int layoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityResultLauncher<String> requestPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                }
        );
        requestPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION);

    }

    @Override
    protected void onStart() {
        super.onStart();
        startLocationService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationService();

    }

    private void startLocationService() {
        if (!isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationServiceUpdate.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);

        }
    }

    private void stopLocationService() {
        if (isLocationServiceRunning()) {
            Intent intent = new Intent(getApplicationContext(), LocationServiceUpdate.class);
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
            startService(intent);
        }

    }

    private boolean isLocationServiceRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null) {
            for (ActivityManager.RunningServiceInfo service :
                    activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if (LocationServiceUpdate.class.getName().equals(service.service.getClassName())) {
                    if (service.foreground) {
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

}