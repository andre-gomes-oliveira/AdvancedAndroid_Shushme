package com.example.android.shushme;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class Geofencing implements ResultCallback {
    private final String TAG = Geofencing.class.getSimpleName();

    private List<Geofence> mGeofenceList;
    private PendingIntent mGeofencePendingIntent;
    private GoogleApiClient mGoogleApiClient;
    private Context mContext;

    public Geofencing(@Nullable List<Geofence> geofenceList,
                      @Nullable PendingIntent geofencePendingIntent,
                      @NonNull GoogleApiClient mGoogleApiClient,
                      @NonNull Context mContext) {
        if (geofenceList != null && geofenceList.size() > 0)
            this.mGeofenceList = geofenceList;
        else
            this.mGeofenceList = new ArrayList<>();

        if (geofencePendingIntent != null)
            this.mGeofencePendingIntent = geofencePendingIntent;

        this.mGoogleApiClient = mGoogleApiClient;
        this.mContext = mContext;
    }

    public void updateGeofencesList(@NonNull PlaceBuffer places) {
        mGeofenceList = new ArrayList<>();

        if (places.getCount() > 0) {
            for (Place place : places) {
                final String id = place.getId();
                final double latitude = place.getLatLng().latitude;
                final double longitude = place.getLatLng().longitude;

                Geofence geofence = new Geofence.Builder()
                        .setRequestId(id)
                        .setExpirationDuration(360000)
                        .setCircularRegion(latitude, longitude, 1000)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build();

                mGeofenceList.add(geofence);
            }
        }
    }

    public void registerAllGeofences() {
        if (mGoogleApiClient != null &&
                mGoogleApiClient.isConnected() &&
                mGeofenceList != null &&
                mGeofenceList.size() > 0) {

            final GeofencingClient geofencingClient = LocationServices.getGeofencingClient(mContext);

            if (ActivityCompat.checkSelfPermission(mContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            geofencingClient.addGeofences(getGeofencingRequest(), getGeofencePendingIntent());
        }
    }

    public void unregisterAllGeofences() {
        final GeofencingClient geofencingClient = LocationServices.getGeofencingClient(mContext);

        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
            return;

        geofencingClient.removeGeofences(getGeofencePendingIntent());
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);

        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {

        if (mGeofencePendingIntent == null) {
            Intent intent = new Intent(mContext, GeofenceBroadcastReceiver.class);

            mGeofencePendingIntent = PendingIntent.getBroadcast(mContext,
                    88,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return mGeofencePendingIntent;
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.e(TAG, "something happened: " + result.getStatus().toString());
    }
}
