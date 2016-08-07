package com.lamadesign.smartalarm.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lamadesign.smartalarm.Activities.AlarmRingActivity;

/**
 * Created by Adam on 07.08.2016.
 */
//musi se zavolat AlarmReceiver.completeWakefulIntent(intent); abz slo cpu spat
//existuje jeste bez wakeful pry neni jistota ze se behem vykonavani uspi cpu
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //jinak co chci delat pri probuzeni

        Intent wakeUp = new Intent(context, AlarmRingActivity.class);
        wakeUp.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(wakeUp);
    }
}
