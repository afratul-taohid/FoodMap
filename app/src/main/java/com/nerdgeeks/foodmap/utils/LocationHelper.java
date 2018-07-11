package com.nerdgeeks.foodmap.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Places;

public class LocationHelper {

    private static LocationHelper locationHelper;
    private Location lastLocation;

    private FusedLocationProviderClient mFusedLocationClient;
    private GoogleApiClient client;
    private LocationRequest mLocationRequest;
    private final int REQUEST_LOCATION = 199;
    private final int LOCATION_PERMISSION_ID = 1001;
    private Context context;
    private LocationCallBack locationCallBack;

    public interface LocationCallBack {

        void onLocationCallBack(Location location);
    }

    private LocationHelper(Context context){
        this.context = context;
    }

    public static LocationHelper with(Context context) {
        if (locationHelper == null){
            locationHelper = new LocationHelper(context);
        }
        return locationHelper;
    }

    public Location getLastLocation(){
        return lastLocation;
    }

    public GoogleApiClient getGoogleApiClient() {
        return client;
    }

    public void updateLocation(LocationCallBack locationCallBack) {
        this.locationCallBack = locationCallBack;
        if (client != null && client.isConnected()){
            checkLocationPermission();
        } else {
            initialize();
        }
    }

    private synchronized void initialize(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        client = new GoogleApiClient
                .Builder(context)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        checkLocationPermission();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();
        client.connect();
    }

    private void checkLocationPermission() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            }
            return;
        }

        askForGPS();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            askForGPS();
        } else {
            showQuitPopup("Location Permission Required");
        }
    }

    private void askForGPS(){

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setNeedBle(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        loadFirstTime();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            status.startResolutionForResult((Activity) context, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException ignored) {}
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_LOCATION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        loadFirstTime();
                        break;
                    case Activity.RESULT_CANCELED:
                        showQuitPopup("Please enable GPS");
                        break;
                }
                break;
        }
    }

    private void loadFirstTime() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_ID);
            }
            return;
        }

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            lastLocation = locationResult.getLastLocation();

            locationCallBack.onLocationCallBack(lastLocation);

            if (mFusedLocationClient != null) {
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            }
        }


    };


    private void showQuitPopup(String message){

        MaterialDialog.Builder builder =  new MaterialDialog.Builder(context)
                .canceledOnTouchOutside(false)
                .title(message)
                //.customView(R.layout.exit_dialog,false)
                .negativeText("Try Again")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        initialize();
                    }
                })
                .positiveText("Exit App")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Activity activity = (Activity) context;
                        activity.finish();
                        System.exit(1);
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }
}