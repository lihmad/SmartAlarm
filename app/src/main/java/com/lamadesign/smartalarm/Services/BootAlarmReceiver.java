package com.lamadesign.smartalarm.Services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by Adam on 28.08.2016.
 */
public class BootAlarmReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AlarmService.class);
        i.putExtra("Wakefull", "Wakefull");
        startWakefulService(context, i);
    }
}
