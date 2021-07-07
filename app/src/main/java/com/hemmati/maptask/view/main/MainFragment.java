package com.hemmati.maptask.view.main;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.carto.core.ScreenBounds;
import com.carto.core.ScreenPos;
import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hemmati.maptask.BuildConfig;
import com.hemmati.maptask.R;
import com.hemmati.maptask.base.BaseFragment;
import com.hemmati.maptask.util.MarkerStyleEnum;
import com.hemmati.maptask.util.ViewModelFactory;
import com.hemmati.maptask.viewModel.DirectionViewModel;

import org.jetbrains.annotations.NotNull;
import org.neshan.common.model.LatLng;
import org.neshan.common.model.LatLngBounds;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import static com.hemmati.maptask.util.Constants.ACTION_BROADCAST;
import static com.hemmati.maptask.util.Constants.EXTRA_EXIT;
import static com.hemmati.maptask.util.Constants.EXTRA_LOCATION;
import static com.hemmati.maptask.util.Constants.FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.hemmati.maptask.util.Constants.LAT_KEY;
import static com.hemmati.maptask.util.Constants.LNG_KEY;
import static com.hemmati.maptask.util.Constants.SEARCH_RESULT_KEY;
import static com.hemmati.maptask.util.Constants.UPDATE_INTERVAL_IN_MILLISECONDS;
import static com.hemmati.maptask.util.MarkerStyleEnum.DIRECTION_MODE;
import static com.hemmati.maptask.util.MarkerStyleEnum.GPS_OFF;
import static com.hemmati.maptask.util.MarkerStyleEnum.GPS_ON;

public class MainFragment extends BaseFragment {

    private static final String TAG = "MainFragment";
    public static final String CAR_KEY = "car";
    private static final String MOTOR_KEY = "motorcycle";
    private static final int REQUEST_CODE = 20;


    private Animation fromBottom;
    private Animation toBottom;
    AnimationStyle animSt;

    private FloatingActionButton fabDirection, fabLocation, fabSetting, fabCar, fabMotor;
    private CardView searchCardView, navigateCardView, directionCard;

    private Polyline onMapPolyline;
    private Location userLocation;
    private TextView tvAddress, tvTime, tvKm;
    private ConstraintLayout clNavigate;
    private Marker currentMarker, targetMarker;
    MapView map;

    private boolean isDirection = false;
    private boolean closed = false;
    private String currentLocation = "";
    private String targetLocation = "";
    private String type;
    private double bearing = 30;

    private DirectionViewModel viewModel;

    @Inject
    ViewModelFactory viewModelFactory;


    @Override
    protected int layoutRes() {
        return R.layout.fragment_main;
    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initAnim();
        initViewModel();
        actionClicks(view);
        gpsChangedToggleView(getGpsState());
        handlingBackButton();
        observeDialogData();

    }

    private void observeDialogData() {
        NavController navController = NavHostFragment.findNavController(this);
        MutableLiveData<Bundle> liveData = Objects.requireNonNull(navController.getCurrentBackStackEntry())
                .getSavedStateHandle()
                .getLiveData(SEARCH_RESULT_KEY);
        liveData.observe(getViewLifecycleOwner(), bundle -> getNavigate(
                new LatLng(bundle.getDouble(LAT_KEY), bundle.getDouble(LNG_KEY))));
    }

    private void getNavigate(LatLng latLng) {
        if (isDirectionMode())
            return;
        targetLocation = latLngToString(latLng);


        if (!getGpsState()) {
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(),
                    getString(R.string.gps_active_msg), Toast.LENGTH_SHORT).show());
        } else if (isNavigateMode()) {
            if (targetMarker != null)
                map.removeMarker(targetMarker);

            targetMarker = createMarker(latLng);
            map.addMarker(targetMarker);

            viewModel.getAddress(latLng.getLatitude(), latLng.getLongitude());
            viewModel.getNavigateRequest(currentLocation, targetLocation);

            moveToCameraBounds();
        } else {
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(),
                    getString(R.string.user_location_null_msg), Toast.LENGTH_SHORT).show());

        }

    }

    private void moveToCameraBounds() {
        map.setTilt(90, 1);

        if (TextUtils.isEmpty(targetLocation))
            return;

        addUserMarker(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), GPS_ON);

        String[] latLngList = targetLocation.split(",");
        LatLng latLng = new LatLng(Double.parseDouble(latLngList[0]), Double.parseDouble(latLngList[1]));
        LatLngBounds lngBounds = new LatLngBounds(latLng, new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));

        map.moveToCameraBounds(lngBounds, new ScreenBounds(new ScreenPos(100f, 200f),
                        new ScreenPos(getScreenWidth() - 100, getScreenHeight() - 200)),
                false, true, true, 1);

    }

    private float getScreenHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    private float getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    private double bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double lng2 = latLng2.getLongitude();
        double lng1 = latLng1.getLongitude();
        double lat2 = latLng2.getLatitude();
        double lat1 = latLng1.getLatitude();


        double dLon = (lng1 - lng2);
        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(dLon);
        double brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));

        return brng;
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, viewModelFactory).get(DirectionViewModel.class);

        viewModel.getPolyLine().observe(getViewLifecycleOwner(), this::polylineResponse);

        viewModel.getStep().observe(getViewLifecycleOwner(), this::stepsResponse);

        viewModel.getAddress().observe(getViewLifecycleOwner(), this::addressResponse);

        viewModel.getDistanceKm().observe(getViewLifecycleOwner(), result -> tvKm.setText(result));

        viewModel.getDuration().observe(getViewLifecycleOwner(), result -> tvTime.setText(result));
    }

    private void stepsResponse(List<LatLng> latLngList) {
        if (latLngList.size() >= 2) {
            bearing = bearingBetweenLocations(latLngList.get(0), latLngList.get(1));
        } else
            Toast.makeText(requireContext(), getString(R.string.travel_end), Toast.LENGTH_SHORT).show();
    }

    private void addressResponse(String address) {
        navigateCardView.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.Tada)
                .duration(700)
                .playOn(navigateCardView);
        tvAddress.setText(address);
    }

    private void polylineResponse(Polyline polyline) {
        if (polyline == null)
            return;
        if (isNavigateMode()) {
            if (onMapPolyline != null)
                map.removePolyline(onMapPolyline);

            map.addPolyline(polyline);
            onMapPolyline = polyline;

        }
        if (isDirectionMode()) {

            mapSetPosition();
            directionCard.setVisibility(View.VISIBLE);
            clNavigate.setVisibility(View.GONE);

        }
    }

    private void mapSetPosition() {
        double centerFirstMarkerX = userLocation.getLatitude();
        double centerFirstMarkerY = userLocation.getLongitude();
        map.moveCamera(new LatLng(centerFirstMarkerX, centerFirstMarkerY), 0.5f);
        map.setZoom(18, 0.5f);
        map.setTilt(30, 1);
        map.setBearing((float) bearing, 1);

    }

    private void onLocationChange(boolean gpsState) {
        if (userLocation != null) {
            addUserMarker(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), gpsState ? GPS_ON : GPS_OFF);
            map.moveCamera(
                    new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 0.25f);

            map.setZoom(15, 0.25f);
        }
    }

    private void addUserMarker(LatLng loc, MarkerStyleEnum style) {
        if (currentMarker != null) {
            map.removeMarker(currentMarker);
        }
        int bitmapRes;
        float size = 30f;
        if (DIRECTION_MODE.equals(style)) {
            bitmapRes = R.drawable.direction_marker;
            size = 60f;
        } else {
            bitmapRes = R.drawable.ic_marker;
        }

        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(size);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), bitmapRes)));
        MarkerStyle markSt = markStCr.buildStyle();

        currentMarker = new Marker(loc, markSt);

        map.addMarker(currentMarker);
    }

    public void focusOnUserLocation() {
        if (userLocation != null) {
            focusOnLocation(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()));
        }
    }

    private void focusOnLocation(LatLng latLng) {
        map.moveCamera(latLng, 0.25f);
        map.setZoom(15, 0.25f);

    }

    private void handlingBackButton() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (closed)
                    OnDirectionButtonClick(true);

                if (isDirectionMode()) {
                    moveToCameraBounds();
                    isDirection = false;
                    clNavigate.setVisibility(View.VISIBLE);
                    directionCard.setVisibility(View.GONE);
                } else if (isNavigateMode()) {
                    restViewToLocationMode();

                } else
                    showExitDialog();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

    }

    private void startLocationUpdates() {

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        SettingsClient settingsClient = LocationServices.getSettingsClient(requireContext());


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(getLocationRequest());

        settingsClient
                .checkLocationSettings(builder.build())
                .addOnSuccessListener(requireActivity(), locationSettingsResponse ->
                        fusedLocationClient.requestLocationUpdates(getLocationRequest(), getLocationCallback(), Looper.myLooper()))
                .addOnFailureListener(requireActivity(), this::settingsClientOnFailure);
    }

    private LocationCallback getLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
            }
        };
    }

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    private void settingsClientOnFailure(Exception e) {
        int statusCode = ((ApiException) e).getStatusCode();
        switch (statusCode) {
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    ResolvableApiException rae = (ResolvableApiException) e;
                    rae.startResolutionForResult(requireActivity(), REQUEST_CODE);
                } catch (IntentSender.SendIntentException sie) {
                    sie.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                String errorMessage = "Location settings are inadequate, and cannot be " +
                        "fixed here. Fix in Settings.";
                Log.e(TAG, errorMessage);
        }
    }

    private void restViewToLocationMode() {
        if (onMapPolyline != null)
            map.removePolyline(onMapPolyline);

        if (targetMarker != null) {
            map.removeMarker(targetMarker);
            navigateCardView.setAnimation(null);
            navigateCardView.setVisibility(View.GONE);
            focusOnUserLocation();
        }
        targetLocation = "";
        isDirection = false;

    }

    private void showExitDialog() {
        NavDirections action =
                MainFragmentDirections
                        .actionMainFragmentToExitDialogFragment();
        NavController navController = NavHostFragment.findNavController(this);

        navController.navigate(action);


    }

    private void initView(View view) {
        fabDirection = view.findViewById(R.id.fab_direction);
        fabLocation = view.findViewById(R.id.fab_location);
        fabSetting = view.findViewById(R.id.fab_setting);
        fabCar = view.findViewById(R.id.fab_car);
        fabMotor = view.findViewById(R.id.fab_motor);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvTime = view.findViewById(R.id.tvTime);
        tvKm = view.findViewById(R.id.tvKm);

        searchCardView = view.findViewById(R.id.cardView);
        navigateCardView = view.findViewById(R.id.navigateCardView);
        directionCard = view.findViewById(R.id.directionCard);
        clNavigate = view.findViewById(R.id.clNavigate);
        map = view.findViewById(R.id.map);

    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void initAnim() {
        fromBottom = AnimationUtils.loadAnimation(getContext(), R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(getContext(), R.anim.to_bottom_anim);
    }

    private void actionClicks(View view) {
        fabCar.setOnClickListener(this::carFabAction);
        fabMotor.setOnClickListener(this::motorFabAction);
        fabDirection.setOnClickListener(this::directionFabAction);
        searchCardView.setOnClickListener(v -> searchCardViewAction(view));
        fabLocation.setOnClickListener(this::locationFabAction);
        fabSetting.setOnClickListener(this::settingFabAction);
        map.setOnMapLongClickListener(this::getNavigate);

    }

    private void settingFabAction(View v) {
        animateFab(v);
        openSettings();
    }

    private void locationFabAction(View v) {
        if (getGpsState()) {
            focusOnUserLocation();
        } else {
            startLocationUpdates();
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(),
                    getString(R.string.gps_active_msg), Toast.LENGTH_SHORT).show());

        }
        animateFab(v);
    }

    private void searchCardViewAction(View v) {
        if (getGpsState() && userLocation != null) {
            NavDirections action =
                    MainFragmentDirections
                            .actionMainFragmentToSearchFragment(String.valueOf(userLocation.getLatitude()),
                                    String.valueOf(userLocation.getLongitude()));
            Navigation.findNavController(v).navigate(action);
        } else {
            startLocationUpdates();
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(),
                    getString(R.string.gps_active_msg), Toast.LENGTH_SHORT).show());
        }
    }

    private void directionFabAction(View v) {
        if (isNavigateMode()) {
            OnDirectionButtonClick(closed);
        } else
            requireActivity().runOnUiThread(() -> Toast.makeText(requireContext(),
                    getString(R.string.direction_error_msg), Toast.LENGTH_SHORT).show());
    }

    private void motorFabAction(View v) {
        OnDirectionButtonClick(true);
        isDirection = true;
        type = MOTOR_KEY;
        viewModel.getDirection(MOTOR_KEY, currentLocation, targetLocation);
    }

    private void carFabAction(View v) {
        OnDirectionButtonClick(true);
        isDirection = true;
        type = CAR_KEY;
        viewModel.getDirection(CAR_KEY, currentLocation, targetLocation);
    }

    private boolean isNavigateMode() {
        return !TextUtils.isEmpty(targetLocation) && userLocation != null;
    }

    private boolean isDirectionMode() {
        return isNavigateMode() && isDirection;
    }

    private String latLngToString(LatLng LatLng) {
        return LatLng.getLatitude() + "," + LatLng.getLongitude();
    }

    private Marker createMarker(LatLng loc) {

        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        animSt = animStBl.buildStyle();


        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_marker_blue)));

        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();


        return new Marker(loc, markSt);
    }

    private void animateFab(View v) {
        YoYo.with(Techniques.Bounce)
                .duration(700)
                .playOn(v);
    }

    private void OnDirectionButtonClick(boolean closed) {
        setVisibility(closed);
        setAnimation(closed);
        this.closed = !closed;
    }

    private void setAnimation(boolean closed) {

        if (!closed) {
            fabCar.startAnimation(fromBottom);
            fabMotor.startAnimation(fromBottom);
        } else {
            fabCar.startAnimation(toBottom);
            fabMotor.startAnimation(toBottom);
        }
    }

    private void setVisibility(boolean closed) {
        if (!closed) {
            fabCar.setVisibility(View.VISIBLE);
            fabMotor.setVisibility(View.VISIBLE);
        } else {
            fabCar.setVisibility(View.INVISIBLE);
            fabMotor.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        requireActivity().unregisterReceiver(locationSwitchStateReceiver);
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(locationUpdateReceiver);

    }

    @Override
    public void onStart() {
        super.onStart();
        startLocationUpdates();
        IntentFilter filter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        filter.addAction(Intent.ACTION_PROVIDER_CHANGED);
        requireActivity().registerReceiver(locationSwitchStateReceiver, filter);

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(locationUpdateReceiver,
                new IntentFilter(ACTION_BROADCAST));
    }


    private final BroadcastReceiver locationSwitchStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

                gpsChangedToggleView(getGpsState());

                userLocation = null;

            }
        }
    };

    private final BroadcastReceiver locationUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean exit = Objects.requireNonNull(intent.getExtras()).getBoolean(EXTRA_EXIT, false);
            if (exit)
                requireActivity().finish();
            Location location = intent.getParcelableExtra(EXTRA_LOCATION);
            if (location != null) {
                if (isDirectionMode()) {
                    directionUiMode(location);

                } else if (userLocation == null) {

                    userLocation = location;
                    onLocationChange(getGpsState());
                } else {

                    userLocation = location;
                }
                currentLocation = location.getLatitude() + "," + location.getLongitude();
            }
        }
    };

    private void directionUiMode(Location location) {
        userLocation = location;
        addUserMarker(new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), DIRECTION_MODE);
        map.moveCamera(
                new LatLng(userLocation.getLatitude(), userLocation.getLongitude()), 0.25f);
        viewModel.getDirection(type, currentLocation, targetLocation);
    }


    private boolean getGpsState() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = Objects.requireNonNull(locationManager).isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsEnabled || isNetworkEnabled;
    }

    private void gpsChangedToggleView(boolean state) {

        if (state) {
            fabLocation.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location));

        } else {
            fabLocation.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_disable));

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.d(TAG, "User agreed to make required location settings changes.");
                    break;
                case Activity.RESULT_CANCELED:
                    Log.d(TAG, "User chose not to make required location settings changes.");

                    break;
            }
        }
    }

}