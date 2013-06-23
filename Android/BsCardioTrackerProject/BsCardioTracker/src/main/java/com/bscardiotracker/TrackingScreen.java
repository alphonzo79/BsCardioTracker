package com.bscardiotracker;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bscardiotracker.data.WorkoutDataEntity;
import com.bscardiotracker.data.WorkoutHistoryDAO;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

public class TrackingScreen extends FragmentActivity implements TimerService.TimerListener {
    private GoogleMap map;
    private Polyline polyLine;
    private PolylineOptions polyLineOptions;

    private final int DISTANCE_UPDATE_INTERVAL = 10;
    private double distance;
    private int pace;
    private LatLng lastLatLng;

    TimerService timerService;
    boolean timerServiceBound;

    GpsService gpsService;
    boolean gpsServiceBound;

    TextView timeDisplay, distanceDisplay, paceDisplay;
    Button startStopButton, pauseRestartButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_screen);

        this.bindService(new Intent(this, TimerService.class), timerConnection, BIND_AUTO_CREATE);
        this.bindService(new Intent(this, GpsService.class), gpsConnection, BIND_AUTO_CREATE);

        distance = 0d;

        timeDisplay = (TextView)findViewById(R.id.time_display);
        distanceDisplay = (TextView)findViewById(R.id.distance_display);
        paceDisplay = (TextView)findViewById(R.id.pace_display);

        startStopButton = (Button)findViewById(R.id.start_stop_button);
        startStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timerService.startTimer();
                startStopButton.setText(R.string.stop);

                startStopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        timerService.stopTimer();

                        WorkoutDataEntity workout = new WorkoutDataEntity();
                        workout.setWorkoutDate(Calendar.getInstance().getTimeInMillis());
                        workout.setDuration(timerService.getSeconds());
                        workout.setDistance(distance);
                        workout.setPace(pace);
                        workout.setLocations(gpsService.getAllLocations());

                        WorkoutHistoryDAO db = new WorkoutHistoryDAO(TrackingScreen.this);
                        db.recordNewWorkout(workout);

                        Intent summaryScreen = new Intent(TrackingScreen.this, SummaryScreen.class);
                        summaryScreen.putExtra("entity", workout);
                        startActivity(summaryScreen);
                        TrackingScreen.this.finish(); //Since this screen contains state that has just been finalized, we don't want to give the user the opportunity to alter it
                    }
                });

                pauseRestartButton.setEnabled(true);
            }
        });

        pauseRestartButton = (Button)findViewById(R.id.pause_button);
        pauseRestartButton.setOnClickListener(pauseTimer);
        pauseRestartButton.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();

        setUpMap();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(gpsConnection);
        unbindService(timerConnection);
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
            options.setScrollGesturesEnabled(true);
            LatLng startLocation = gpsService.getStartLocation();
            Log.d("CardioTracker", "Start Location: " + startLocation);
            if(startLocation != null) {
                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(startLocation, 16, 0, 0)));
            }
        }
    }

    Button.OnClickListener pauseTimer = new Button.OnClickListener(){
        @Override
        public void onClick(View view) {
            timerService.pauseTimer();
            pauseRestartButton.setText(R.string.resume);
            pauseRestartButton.setOnClickListener(resumeTimer);
        }
    };

    Button.OnClickListener resumeTimer = new Button.OnClickListener() {
        @Override
        public void onClick(View view) {
            timerService.startTimer();
            pauseRestartButton.setText(R.string.pause);
            pauseRestartButton.setOnClickListener(pauseTimer);
        }
    };

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
            unbindService(timerConnection);
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
            unbindService(gpsConnection);
            gpsServiceBound = false;
        }
    };

    @Override
    public void updateTime(int numSeconds) {
        updateTimeDisplay(numSeconds);
        updateMap(numSeconds);
    }

    private void updateTimeDisplay(int numSeconds) {
        int second = numSeconds % 60;
        int minutes = numSeconds / 60;
        timeDisplay.setText(String.format("%02d:%02d", minutes, second));
    }

    private void updateMap(int numSeconds) {
        List<LatLng> newLocations = gpsService.getNewLocations();
        if(newLocations != null && newLocations.size() > 0) {
            LatLng newCenter = newLocations.get(newLocations.size() - 1);
            if(newCenter != null) {
                float zoom = map.getCameraPosition().zoom;
                map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(newCenter, zoom, 0, 0)));
            }

            if(polyLine == null) {
                polyLineOptions = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
                polyLine = map.addPolyline(polyLineOptions);
            }

            for(LatLng location : newLocations) {
                polyLineOptions = polyLineOptions.add(location);
            }

            polyLine.setPoints(polyLineOptions.getPoints());

            updateDistance(newLocations);
            updatePace(numSeconds);
        }
    }

    private void updateDistance(List<LatLng> newLocations) {
        //http://en.wikipedia.org/wiki/Haversine_formula
        //http://stackoverflow.com/questions/120283/working-with-latitude-longitude-values-in-java
        for(LatLng location : newLocations) {
            if(lastLatLng != null) {
                double earthRadiusInMiles = 3958.75;
                double latDiff = Math.toRadians(location.latitude - lastLatLng.latitude);
                double lngDiff = Math.toRadians(location.longitude - lastLatLng.longitude);
                double sinLatDiff = Math.sin(latDiff / 2);
                double sinLngDiff = Math.sin(lngDiff / 2);
                double a = Math.pow(sinLatDiff, 2)
                        + Math.pow(sinLngDiff, 2)
                        * Math.cos(Math.toRadians(lastLatLng.latitude))
                        *  Math.cos(Math.toRadians(location.latitude));
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                double dist = earthRadiusInMiles * c;

                distance += dist;
            }
            lastLatLng = location;
        }

        BigDecimal bd = new BigDecimal(distance);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        distanceDisplay.setText(bd.toPlainString());
    }

    private void updatePace(int numSeconds) {
        //minutes per mile
        Double paceInSeconds = numSeconds / distance;
        pace = paceInSeconds.intValue();

        if(pace < 5999) { //99:59
            int seconds = pace % 60;
            int minutes = pace / 60;

            paceDisplay.setText(String.format("%02d:%02d", minutes, seconds));
        } else {
            paceDisplay.setText(R.string.clock_default);
        }
    }
}
