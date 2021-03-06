package com.lamadesign.smartalarm.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lamadesign.smartalarm.Fragments.AlarmSettingsFragment;
import com.lamadesign.smartalarm.R;

public class AlarmSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_alarm_settings);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new AlarmSettingsFragment()).commit();
    }
}
