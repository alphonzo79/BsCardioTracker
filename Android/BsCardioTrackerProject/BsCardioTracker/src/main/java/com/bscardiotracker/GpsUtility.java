package com.bscardiotracker;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class GpsUtility {
    LocationManager locationManager;
    Location lastKnownLocation;
    Context context;

    public GpsUtility(Context context) {
        this.context = context;
    }

    public void setUpLocationService(){
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

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
    }

    public LatLng getLocation() {
        try{
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation == null){
                tryCourseLocation();
            }
        }
        catch(SecurityException e){
            tryCourseLocation();
        }
        catch(IllegalArgumentException e){
            tryCourseLocation();
        }

        if(lastKnownLocation != null) {
            return new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
        } else {
            return null;
        }
    }

    private Location tryCourseLocation(){
        try{
            lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        catch(SecurityException e){
            lastKnownLocation = null;
        }
        catch(IllegalArgumentException e){
            lastKnownLocation = null;
        }
        return lastKnownLocation;
    }
}
