package com.lamadesign.smartalarm.Services;

import android.app.AlarmManager;
import android.app.PendingIntent;
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

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
    }

    public void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
