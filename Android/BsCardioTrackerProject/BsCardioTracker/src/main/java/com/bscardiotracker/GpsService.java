package com.bscardiotracker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GpsService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}