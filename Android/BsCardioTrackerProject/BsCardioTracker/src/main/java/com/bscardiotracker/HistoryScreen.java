package com.bscardiotracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bscardiotracker.data.WorkoutDataEntity;
import com.bscardiotracker.data.WorkoutHistoryDAO;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryScreen extends Activity {
    List<WorkoutDataEntity> workoutHistory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_screen);

        WorkoutHistoryDAO db = new WorkoutHistoryDAO(this);
        workoutHistory = db.getRecentWorkouts();

        ListView listView = (ListView)findViewById(R.id.history_list);
        listView.setAdapter(new HistoryListAdapter(workoutHistory));
    }

    private class HistoryListAdapter extends BaseAdapter {
        List<WorkoutDataEntity> data;

        public HistoryListAdapter(List<WorkoutDataEntity> data) {
            this.data = data;
        }

        @Override
        public int getCount() {
            return data != null ? data.size() : 0;
        }

        @Override
        public WorkoutDataEntity getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View root = getLayoutInflater().inflate(R.layout.history_list_layout, null);

            TextView header = (TextView)root.findViewById(R.id.history_cell_header);
            TextView time = (TextView)root.findViewById(R.id.history_cell_duration_display);
            TextView distance = (TextView)root.findViewById(R.id.history_cell_distance_display);

            WorkoutDataEntity workout = data.get(i);

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy h:mm a");
            Date date = new Date(workout.getWorkoutDate());
            header.setText(sdf.format(date));

            int duration = workout.getDuration();
            int second = duration % 60;
            int minutes = duration / 60;
            time.setText(String.format("%02d:%02d", minutes, second));

            BigDecimal bd = new BigDecimal(workout.getDistance());
            bd = bd.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            distance.setText(bd.toPlainString());

            final String json = new Gson().toJson(workout);

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent summaryScreen = new Intent(HistoryScreen.this, SummaryScreen.class);
                    Log.d("CardioTracker", json);
                    summaryScreen.putExtra("entity", json);
                    startActivity(summaryScreen);
                }
            });

            return root;
        }
    }
}
