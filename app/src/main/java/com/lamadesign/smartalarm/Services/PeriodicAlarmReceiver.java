package com.lamadesign.smartalarm.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Adam on 15.08.2016.
 */
public class PeriodicAlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("PeriodicAlarmReceiver", "tu");
        Intent i = new Intent(context, AlarmService.class);
        context.startService(i);
    }
}
