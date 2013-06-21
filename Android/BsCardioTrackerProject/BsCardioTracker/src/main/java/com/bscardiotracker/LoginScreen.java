package com.bscardiotracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class LoginScreen extends Activity {

    //todo
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(this, Dashboard.class));
    }
}
