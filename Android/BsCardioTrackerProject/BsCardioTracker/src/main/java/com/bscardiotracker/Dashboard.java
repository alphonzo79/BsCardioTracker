package com.bscardiotracker;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.bscardiotracker.R;

public class Dashboard extends Activity {
    Button trackButton;
    Button historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        trackButton = (Button)findViewById(R.id.track_button);
        trackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent launchTracker = new Intent(Dashboard.this, TrackingScreen.class);
                startActivity(launchTracker);
            }
        });

        historyButton = (Button)findViewById(R.id.history_button);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyTracker = new Intent(Dashboard.this, HistoryScreen.class);
                startActivity(historyTracker);
            }
        });
    }
}
