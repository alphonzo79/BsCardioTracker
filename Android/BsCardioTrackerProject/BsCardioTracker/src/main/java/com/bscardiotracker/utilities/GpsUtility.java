package com.bscardiotracker.utilities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class GpsUtility {
    LocationManager locationManager;
    Location lastKnownLocation;
    Context context;

    public GpsUtility(Context context) {
        this.context = context;
    }

    public void setUpLocationService(){
        Log.d("CardioTracker", "Setting up GPS Utility");
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if(locationManager == null) {
            Log.d("CardioTracker", "Location Manager was null");
        }

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                lastKnownLocation = location;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        getLocation();
    }

    public LatLng getLocation() {
        try{
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation == null){
                Log.d("CardioTracker", "Fine location failed. Trying coarse location");
                tryCourseLocation();
            }
        }
        catch(SecurityException e){
            Log.d("CardioTracker", "Caught security exception. Trying coarse location");
            tryCourseLocation();
        }
        catch(IllegalArgumentException e){
            Log.d("CardioTracker", "Caught illegal arbument exception. Trying coarse location");
            tryCourseLocation();
        }

        if(lastKnownLocation != null) {
            Log.d("CardioTracker", "lastKnownLocation was good. Returning a LatLng from it");
            return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } else {
            Log.d("CardioTracker", "lastKnownLocation was null");
            return null;
        }
    }

    private Location tryCourseLocation(){
        try{
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch(SecurityException e){
            Log.d("CardioTracker", "Caught security exception");
            lastKnownLocation = null;
        }
        catch(IllegalArgumentException e){
            Log.d("CardioTracker", "Caught illegal argument exception");
            lastKnownLocation = null;
        }

        if(lastKnownLocation == null) {
            Log.d("CardioTracker", "returning from Course Location. Location was null");
        }

        return lastKnownLocation;
    }
}
