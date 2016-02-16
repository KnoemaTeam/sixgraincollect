package org.graindataterminal.network;

import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import org.odk.collect.android.application.Collect;

import java.util.ArrayList;
import java.util.List;

public class LocationService {
    private static final int OBTAIN_PERIOD = 5000;
    private static final int MIN_GPS_LOCATIONS = 2;
    private static final int MIN_NETWORK_LOCATIONS = 2;
    private static final int TWO_MINUTES = 1000 * 60 * 2;

    private static LocationService instance = null;

    private Location location = null;
    private List<Location> temporaryLocations = new ArrayList<>();
    private int gpsTemporaryLocations = 0;
    private int networkTemporaryLocations = 0;
    private boolean gpsEnabled = false;
    private boolean networkEnabled = false;

    private LocationListener gpsLocationListener = null;
    private LocationListener networkLocationListener = null;
    private GpsStatus.Listener gpsStatusListener = null;

    private LocationManager manager = null;
    private LocationService() {
        obtainLocation();
    }

    public static LocationService getInstance() {
        if (instance == null) {
            instance = new LocationService();
        }
        return instance;
    }

    public Location getLocation() {
        obtainLocation();
        return location;
    }

    private void setLocation(Location location) {
        this.location = location;
        dumpLocation("Location:", location);
    }

    public GpsStatus.Listener getGpsStatusListener () {
        if (gpsStatusListener == null) {
            gpsStatusListener = new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    switch (event) {
                        case GpsStatus.GPS_EVENT_FIRST_FIX:
                            Log.i(getClass().getSimpleName(), "GPS RECEIVED");
                            break;

                        case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                            Log.i(getClass().getSimpleName(), "GPS SATELLITE");
                            break;

                        case GpsStatus.GPS_EVENT_STARTED:
                            Log.i(getClass().getSimpleName(), "GPS STARTED");
                            break;

                        case GpsStatus.GPS_EVENT_STOPPED:
                            Log.i(getClass().getSimpleName(), "GPS STOPPED");
                            break;
                    }
                }
            };
        }

        return gpsStatusListener;
    }

    public LocationListener getGpsLocationListener() {
        if (gpsLocationListener == null) {
            gpsLocationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    try {
                        addTemporaryLocation(location);
                        gpsTemporaryLocations++;

                        if (gpsTemporaryLocations >= MIN_GPS_LOCATIONS) {
                            LocationManager manager = getManager();
                            manager.removeUpdates(gpsLocationListener);
                        }
                    }
                    catch (SecurityException exception) {
                        exception.printStackTrace();
                    }

                    checkFinishObtaining();
                }

                public void onProviderDisabled(String provider) {
                    Log.i(getClass().getSimpleName(), "GPS Location Disabled");
                }

                public void onProviderEnabled(String provider) {
                    Log.i(getClass().getSimpleName(), "GPS Location Enabled");
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.i(getClass().getSimpleName(), "GPS Location Status");
                }
            };
        }

        return gpsLocationListener;
    }

    public LocationListener getNetworkLocationListener() {
        if (networkLocationListener == null) {
            networkLocationListener =  new LocationListener() {
                public void onLocationChanged(Location location) {
                    try {
                        addTemporaryLocation(location);
                        networkTemporaryLocations++;

                        if (networkTemporaryLocations >= MIN_NETWORK_LOCATIONS) {
                            LocationManager manager = getManager();
                            manager.removeUpdates(networkLocationListener);
                        }
                    }
                    catch (SecurityException exception) {
                        exception.printStackTrace();
                    }

                    checkFinishObtaining();
                }

                public void onProviderDisabled(String provider) {
                }

                public void onProviderEnabled(String provider) {
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
            };
        }

        return networkLocationListener;
    }

    private LocationManager getManager() {
        if (manager == null) {
            manager = (LocationManager) Collect.getInstance().getContext().getSystemService(Context.LOCATION_SERVICE);
        }

        return manager;
    }

    private void obtainLocation() {
        setLocation(null);

        Log.d(getClass().getSimpleName(), "Starting obtaining...");
        LocationManager manager = getManager();

        gpsEnabled = false;
        networkEnabled = false;
        gpsTemporaryLocations = 0;
        networkTemporaryLocations = 0;

        try {
            gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.d(getClass().getSimpleName(), "GPS enabled: " + gpsEnabled);
            Log.d(getClass().getSimpleName(), "Network enabled: " + networkEnabled);

            if (gpsEnabled) {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, getGpsLocationListener());
                manager.addGpsStatusListener(getGpsStatusListener());
                addTemporaryLocation(manager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
            }

            if (networkEnabled) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, getNetworkLocationListener());
                addTemporaryLocation(manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
            }

            if (gpsEnabled || networkEnabled)
                setLocation(getBestLocation());
        }
        catch (SecurityException exception) {
            exception.printStackTrace();
        }
    }

    public void finishObtaining() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                obtainBestLocation();
            }
        }, OBTAIN_PERIOD);
    }

    private void obtainBestLocation() {
        try {
            Log.i(getClass().getSimpleName(), "Removed updates listener");
            LocationManager manager = getManager();
            manager.removeUpdates(getGpsLocationListener());
            manager.removeUpdates(getNetworkLocationListener());

            setLocation(getBestLocation());
            clearTemporaryLocations();
        }
        catch (SecurityException exception) {
            exception.printStackTrace();
        }
    }

    private Location getBestLocation() {
        Location bestLocation;
        if (temporaryLocations.size() == 0) {
            bestLocation = null;
        }
        else if (temporaryLocations.size() == 1) {
            bestLocation = temporaryLocations.get(0);
        }
        else {
            bestLocation = temporaryLocations.get(0);
            for (int i = 1; i < temporaryLocations.size(); i++) {
                Location temporaryLocation = temporaryLocations.get(i);
                if (isBetterLocation(temporaryLocation, bestLocation))
                    bestLocation = temporaryLocation;
            }
        }
        return bestLocation;
    }

    private void checkFinishObtaining() {
        if ((!gpsEnabled || gpsTemporaryLocations >= MIN_GPS_LOCATIONS) && (!networkEnabled || networkTemporaryLocations >= MIN_NETWORK_LOCATIONS))
            obtainBestLocation();
    }

    private void addTemporaryLocation(Location location) {
        if (location != null) {
            temporaryLocations.add(location);
            dumpLocation(temporaryLocations.size() + ". Temporary location:", location);
        }
    }

    private void clearTemporaryLocations() {
        temporaryLocations.clear();
        temporaryLocations = new ArrayList<>();
    }

    private boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null)
            return true;

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer)
            return true;
        else if (isSignificantlyOlder)
            return false;

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate)
            return true;
        else if (isNewer && !isLessAccurate)
            return true;
        else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider)
            return true;

        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        return provider1 == null ? provider2 == null : provider1.equals(provider2);
    }

    private void dumpLocation(String messagePrefix, Location location) {
        String message = messagePrefix + " " + (location != null ? location.getProvider() + " Lat=" + location.getLatitude() + " Long=" + location.getLongitude() + " Acc=" + location.getAccuracy() : "null");
        Log.d(getClass().getSimpleName(), message);
    }
}
