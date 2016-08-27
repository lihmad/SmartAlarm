package com.lamadesign.smartalarm.Models;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.services.calendar.model.Event;

/**
 * Created by Adam on 27.08.2016.
 */
public class CalculateAlarmWrapper {
    public Alarm alarm;
    public Context context;
    public com.google.android.gms.maps.model.LatLng[] latLng;

    public CalculateAlarmWrapper(Alarm alarm, Context context, LatLng[] latLng) {
        this.alarm = alarm;
        this.context = context;
        this.latLng = latLng;
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

    public LatLng[] getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng[] latLng) {
        this.latLng = latLng;
    }
}
