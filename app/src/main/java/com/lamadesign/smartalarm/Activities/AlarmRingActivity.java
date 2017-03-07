package com.lamadesign.smartalarm.Activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.lamadesign.smartalarm.R;
import com.lamadesign.smartalarm.Services.AlarmReceiver;

import java.io.IOException;
import java.util.Calendar;

public class AlarmRingActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private Intent fromMain;
    private Intent alarmIntent;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_ring);

        fromMain = getIntent();
        //Log.i("Hour", fromMain.getStringExtra("Hour"));
        alarmIntent = new Intent(AlarmRingActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(AlarmRingActivity.this, 0, alarmIntent, 0);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        ring();


        findViewById(R.id.activity_alarm_ring_button_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                AlarmRingActivity.this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON,
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
        });
    }

    private void ring(){
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        //Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        //ringtone.play();


        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(AlarmRingActivity.this, uri);
            //final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.setLooping(true);
            mediaPlayer.setScreenOnWhilePlaying(true);
            mediaPlayer.prepare();
            mediaPlayer.start();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

/*    public void start(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(fromMain.getStringExtra("Hour")));
        calendar.set(Calendar.MINUTE, Integer.parseInt(fromMain.getStringExtra("Minute")));
        int interval = 8000;

        //alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        alarmManager.setExact(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), pendingIntent);
        }

        //Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show();
    }

    public void cancel(){
        AlarmReceiver.stopAlarm();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //AlarmReceiver.completeWakefulIntent(Althis);

        //Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }*/
}
