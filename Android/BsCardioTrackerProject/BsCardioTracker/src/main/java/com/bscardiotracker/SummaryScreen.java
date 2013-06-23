package com.bscardiotracker;

import android.app.Activity;
import android.os.Bundle;

import com.bscardiotracker.data.WorkoutDataEntity;

import java.io.Serializable;

public class SummaryScreen extends Activity {
    WorkoutDataEntity workout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Serializable extra = getIntent().getSerializableExtra("entity");
        if(extra instanceof WorkoutDataEntity) {
            workout = (WorkoutDataEntity) extra;
        } else {
            workout = null;
        }

        setContentView(R.layout.summary_screen);
    }
    //todo display the summary

    //Todo the api call and display success or failure
}
