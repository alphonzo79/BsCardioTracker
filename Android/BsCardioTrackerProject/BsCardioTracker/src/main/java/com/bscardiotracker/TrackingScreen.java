package com.bscardiotracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.bscardiotracker.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

public class TrackingScreen extends FragmentActivity {
    private GoogleMap mapFrag;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_screen);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(mapFrag == null) {
            mapFrag = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if(mapFrag == null) {
                Toast.makeText(this, "There was a problem getting the map", Toast.LENGTH_LONG).show();
            }
        }
    }
}
