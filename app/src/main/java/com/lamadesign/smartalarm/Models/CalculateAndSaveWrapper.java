package com.lamadesign.smartalarm.Models;

import android.content.Context;
import android.location.Location;

/**
 * Created by Adam on 27.08.2016.
 */
public class CalculateAndSaveWrapper {
    public Alarm alarm;
    public Context context;
    public android.location.Location origin;

    public CalculateAndSaveWrapper(Alarm alarm, Context context, Location origin) {
        this.alarm = alarm;
        this.context = context;
        this.origin = origin;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Location getOrigin() {
        return origin;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }
}
