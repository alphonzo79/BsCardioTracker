package com.bscardiotracker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bscardiotracker.data.WorkoutDataEntity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SummaryScreen extends FragmentActivity {
    WorkoutDataEntity workout;
    private boolean needToLog;

    private GoogleMap map;
    private PolylineOptions polyLineOptions;
    private LatLngBounds mapBounds;

    TextView dateDisplay, timeDisplay, distanceDisplay, paceDisplay;
    Button doneButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String extra = getIntent().getStringExtra("entity");
        if(!TextUtils.isEmpty(extra)) {
            workout = new Gson().fromJson(extra, WorkoutDataEntity.class);
        } else {
            workout = null;
        }

        needToLog = getIntent().getBooleanExtra("needToLog", false);

        setContentView(R.layout.summary_screen);

        dateDisplay = (TextView)findViewById(R.id.date_display);
        timeDisplay = (TextView)findViewById(R.id.time_display);
        distanceDisplay = (TextView)findViewById(R.id.distance_display);
        paceDisplay = (TextView)findViewById(R.id.pace_display);

        doneButton = (Button)findViewById(R.id.done_button);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SummaryScreen.this.finish();
            }
        });

        buildMap();

        if(workout != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
            Date date = new Date(workout.getWorkoutDate());
            dateDisplay.setText(sdf.format(date));

            int duration = workout.getDuration();
            int second = duration % 60;
            int minutes = duration / 60;
            timeDisplay.setText(String.format("%02d:%02d", minutes, second));

            BigDecimal bd = new BigDecimal(workout.getDistance());
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            distanceDisplay.setText(bd.toPlainString());

            int pace = workout.getPace();
            if(pace < 5999) { //99:59
                int paceSeconds = pace % 60;
                int paceMinutes = pace / 60;

                paceDisplay.setText(String.format("%02d:%02d", paceMinutes, paceSeconds));
            } else {
                paceDisplay.setText(R.string.clock_default);
            }
        }

        if(needToLog) {
            //todo api call and display success or failure
        }
    }

    private void buildMap() {
        Log.d("CardioTracker", "setUpMap");
        if(map == null) {
            map = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            if(map == null) {
                Toast.makeText(this, "There was a problem building the map", Toast.LENGTH_LONG).show();
            }
        }

        if(map != null) {
            Log.d("CardioTracker", "Map was not null");
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            UiSettings options = map.getUiSettings();
            options.setCompassEnabled(true);
            options.setZoomControlsEnabled(false);
            options.setZoomGesturesEnabled(true);
            options.setRotateGesturesEnabled(true);
            options.setScrollGesturesEnabled(true);

            buildPolylineAndGetAverageLatLng();

            if(mapBounds != null) {
                //We need to initialize the map first, then fine-tune it with the mapBounds to automatically center and zoom.
                //This auto-center/zoom cannot take place until the map has been drawn, though. Hence the two-steps
                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mapBounds.northeast, 15, 0, 0)), new GoogleMap.CancelableCallback() {
                    @Override
                    public void onFinish() {
                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(mapBounds, 25));
                    }

                    @Override
                    public void onCancel() {
                        //do nothing
                    }
                });
                map.addPolyline(polyLineOptions);
            }
        } else {
            Log.d("CardioTracker", "map Was Null");
        }
    }

    private void buildPolylineAndGetAverageLatLng() {
        List<LatLng> allCheckpoints = workout.getLocations();
        polyLineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for(LatLng location : allCheckpoints) {
            builder.include(location);
            polyLineOptions.add(location);
        }

        mapBounds = builder.build();
    }
}
