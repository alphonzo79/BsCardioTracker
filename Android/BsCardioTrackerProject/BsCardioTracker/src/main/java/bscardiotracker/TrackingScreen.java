package bscardiotracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class TrackingScreen extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_screen);
    }
}
