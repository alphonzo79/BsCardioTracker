package com.bscardiotracker;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class TrackingScreen extends FragmentActivity implements TimerService.TimerListener {
    private GoogleMap map;

    TimerService timerService;
    boolean timerServiceBound;

    GpsService gpsService;
    boolean gpsServiceBound;

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_screen);

        this.bindService(new Intent(this, TimerService.class), timerConnection, BIND_AUTO_CREATE);
        this.bindService(new Intent(this, GpsService.class), gpsConnection, BIND_AUTO_CREATE);
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMap();
    }

    @Override
    public void onDestroy() {
        gpsService.unbindService(gpsConnection);
        timerService.unbindService(timerConnection);
    }

    private void setUpMap() {
        Log.d("CardioTracker", "setUpMap");
        if(map == null) {
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if(map == null) {
                Toast.makeText(this, "There was a problem getting the map", Toast.LENGTH_LONG).show();
            }
        }

        if(map == null) {
            Log.d("CardioTracker", "map Was Null");
        }
        if(!gpsServiceBound) {
            Log.d("CardioTracker", "gpsService was not bound");
        }
        if(map != null && gpsServiceBound) {
            Log.d("CardioTracker", "Map was not null and we are bound to the gps service");
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            UiSettings options = map.getUiSettings();
            options.setCompassEnabled(true);
            options.setZoomControlsEnabled(false);
            options.setZoomGesturesEnabled(true);
            options.setRotateGesturesEnabled(true);
            options.setScrollGesturesEnabled(false);
            LatLng startLocation = gpsService.getStartLocation();
            Log.d("CardioTracker", "Start Location: " + startLocation);
            if(startLocation != null) {
                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(startLocation, 16, 0, 0)));
            }
        }
    }

    private ServiceConnection timerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            timerService = ((TimerService.ServiceBinder)iBinder).getService();
            timerService.addListeners(TrackingScreen.this);

            if(gpsService != null) {
                timerService.addListeners(gpsService);
            }

            timerServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            unbindService(this);
            timerServiceBound = false;
        }
    };

    private ServiceConnection gpsConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("CardioTracker", "gps service connected");
            gpsService = ((GpsService.ServiceBinder)iBinder).getService();
            gpsService.setUpGps(TrackingScreen.this);

            if(timerService != null) {
                timerService.addListeners(gpsService);
            }

            gpsServiceBound = true;

            setUpMap();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d("CardioTracker", "gpsService was disconnected");
            unbindService(this);
            gpsServiceBound = false;
        }
    };

    @Override
    public void updateTime(int numSeconds) {
        //todo
    }
}
