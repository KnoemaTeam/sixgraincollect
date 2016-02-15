package org.graindataterminal.network;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.graindataterminal.controllers.MyApp;
import org.odk.collect.android.application.Collect;

public class LocationEngine implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    protected final static int OBTAIN_INTERVAL = 10000;
    protected final static int OBTAIN_FASTEST_INTERVAL = 5000;
    protected final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    protected static LocationEngine instance = null;
    protected LocationEngineListener engineListener = null;
    protected GoogleApiClient apiClient = null;

    protected boolean isNeedRequestLocationUpdates = false;
    protected boolean isLocationEngineConnected = false;

    protected Location currentLocation = null;
    protected LocationRequest requestLocationUpdates = null;
    protected LocationListener requestLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.i(getClass().getSimpleName(), "onLocationChanged: " + location.toString());

            if (engineListener != null)
                engineListener.updateLocation(location);

            if (!isNeedRequestLocationUpdates)
                stopListenLocationUpdates();
        }
    };

    public interface LocationEngineListener {
        void updateLocation(Location location);
    }

    protected LocationEngine() {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(Collect.getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    public static LocationEngine getInstance() {
        if (instance == null)
            instance = new LocationEngine();

        return instance;
    }

    public void connectLocationEngine(LocationEngineListener engineListener, boolean isNeedRequestLocationUpdates) {
        this.engineListener = engineListener;
        this.isNeedRequestLocationUpdates = isNeedRequestLocationUpdates;

        if (isLocationEngineConnected) {
            Location currentLocation = getLocation();
            if (engineListener != null && currentLocation != null)
                engineListener.updateLocation(currentLocation);

            if (isNeedRequestLocationUpdates)
                startListenLocationUpdates();
        }
        else {
            apiClient.connect();
        }
    }

    public void disconnectLocationEngine() {
        engineListener = null;
        isLocationEngineConnected = false;

        if (isNeedRequestLocationUpdates) {
            isNeedRequestLocationUpdates = false;
            stopListenLocationUpdates();
        }

        apiClient.disconnect();
        Log.i(getClass().getSimpleName(), "disconnectedLocationEngine");
    }

    public Location getLocation() {
        if (!isLocationServicesGranted())
            return null;

        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            if (currentLocation != null) {
                Log.i(getClass().getSimpleName(), currentLocation.toString());
                return currentLocation;
            }
        }
        catch (SecurityException exception) {
            exception.printStackTrace();
        }

        return null;
    }

    protected boolean isLocationServicesGranted() {
        return ActivityCompat.checkSelfPermission(Collect.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Collect.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    protected void createLocationRequest() {
        requestLocationUpdates = LocationRequest.create();
        requestLocationUpdates.setInterval(OBTAIN_INTERVAL);
        requestLocationUpdates.setFastestInterval(OBTAIN_FASTEST_INTERVAL);
        requestLocationUpdates.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startListenLocationUpdates() {
        if (!isLocationServicesGranted())
            return;

        try {
            if (apiClient.isConnected()) {
                Log.i(getClass().getSimpleName(), "startListenLocationUpdates");
                LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, requestLocationUpdates, requestLocationListener);
            }
        }
        catch (SecurityException exception) {
            exception.printStackTrace();
        }
    }

    public void stopListenLocationUpdates() {
        if (!isLocationServicesGranted())
            return;

        try {
            if (apiClient.isConnected()) {
                Log.i(getClass().getSimpleName(), "stopListenLocationUpdates");
                LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, requestLocationListener);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (!isLocationServicesGranted())
            return;

        try {
            Log.i(getClass().getSimpleName(), "onConnected");
            createLocationRequest();

            isLocationEngineConnected = true;
            LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(apiClient);
            Location currentLocation = getLocation();

            if (locationAvailability != null) {
                Log.i(getClass().getSimpleName(), "LocationEngineAvailability: " + String.valueOf(locationAvailability.isLocationAvailable()));

                if (currentLocation == null)
                    startListenLocationUpdates();
            }

            if (engineListener != null && currentLocation != null)
                engineListener.updateLocation(currentLocation);

            if (isNeedRequestLocationUpdates)
                startListenLocationUpdates();
        }
        catch (SecurityException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(getClass().getSimpleName(), "onConnectionFailed: " + connectionResult.getErrorMessage());
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(null, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException exception) {
                exception.printStackTrace();
            }
        } else {
            Log.i(getClass().getSimpleName(), "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(getClass().getSimpleName(), "onConnectionSuspended: " + String.valueOf(i));
    }
}
