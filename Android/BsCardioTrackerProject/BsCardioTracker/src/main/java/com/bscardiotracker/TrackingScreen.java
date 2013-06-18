package com.bscardiotracker;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class TrackingScreen extends FragmentActivity {
    private GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_screen);
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMap();
    }

    private void setUpMap() {
        if(map == null) {
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if(map == null) {
                Toast.makeText(this, "There was a problem getting the map", Toast.LENGTH_LONG).show();
            }
        }

        if(map != null) {
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            UiSettings options = map.getUiSettings();
            options.setCompassEnabled(true);
            options.setZoomControlsEnabled(false);
            options.setZoomGesturesEnabled(true);
            options.setRotateGesturesEnabled(true);
            options.setScrollGesturesEnabled(false);
            map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(43.635, -116.605), 16, 0, 0)));
        }
    }
}
