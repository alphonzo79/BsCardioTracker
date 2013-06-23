package com.bscardiotracker;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    private ServiceBinder serviceBinder = new ServiceBinder();
    private Timer timer;
    private int seconds;
    List<TimerListener> listeners;

    @Override
    public IBinder onBind(Intent intent) {
        return serviceBinder;
    }

    @Override
    public void onCreate() {
        listeners = new ArrayList<TimerListener>();
    }

    @Override
    public void onDestroy() {
        if(timer != null) {
            timer.cancel();
            timer.purge();
        }
        seconds = 0;
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new BbTimerTask(), 1000, 1000);
    }

    public void stopTimer() {
        timer.cancel();
        timer.purge();
    }

    public void pauseTimer() {
        timer.cancel();
        timer.purge();
    }

    public void addListeners(TimerListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TimerListener listener) {
        if(listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public int getSeconds() {
        return seconds;
    }

    public class ServiceBinder extends Binder
    {
        public TimerService getService()
        {
            return TimerService.this;
        }
    }

    final Handler timerTickHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            for(TimerListener listener : listeners) {
                listener.updateTime(seconds);
            }
            return true;
        }
    });

    class BbTimerTask extends TimerTask {
        @Override
        public void run() {
            seconds++;
            timerTickHandler.sendEmptyMessage(0);
        }
    }

    public interface TimerListener {
        public void updateTime(int numSeconds);
    }
}
