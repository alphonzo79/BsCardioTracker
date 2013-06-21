package com.bscardiotracker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class GpsService extends Service implements TimerService.TimerListener {
    private ServiceBinder serviceBinder = new ServiceBinder();

    List<LatLng> fullLocationList;
    List<LatLng> newLocationsList;
    GpsUtility gpsUtility;

    private final int UPDATE_INTERVAL = 5;

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        fullLocationList = new ArrayList<LatLng>();
        newLocationsList = new ArrayList<LatLng>();
    }

    @Override
    public void onDestroy() {

    }

    public void setUpGps(Context context) {
        Log.d("CardioTracker", "Setting Up Gps");
        gpsUtility = new GpsUtility(context);
        gpsUtility.setUpLocationService();
    }

    public List<LatLng> getNewLocations() {
        List<LatLng> retVal = new ArrayList<LatLng>();
        for(LatLng location : newLocationsList) {
            retVal.add(location);
        }
        newLocationsList.clear();
        return retVal;
    }

    public List<LatLng> getAllLocations() {
        return fullLocationList;
    }

    public LatLng getStartLocation() {
        Log.d("CardioTracker", "Getting start location");
        return gpsUtility.getLocation();
    }

    @Override
    public void updateTime(int numSeconds) {
        if(numSeconds % UPDATE_INTERVAL == 0) {
            LatLng location = gpsUtility.getLocation();
            if(location != null) {
                fullLocationList.add(location);
                newLocationsList.add(location);
            }
        }
    }

    public class ServiceBinder extends Binder
    {
        public GpsService getService()
        {
            return GpsService.this;
        }
    }
}
