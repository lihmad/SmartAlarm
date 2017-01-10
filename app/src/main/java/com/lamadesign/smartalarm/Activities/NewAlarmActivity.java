package com.lamadesign.smartalarm.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;

import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.R;
import com.lamadesign.smartalarm.Database.DBOperations;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class NewAlarmActivity extends AppCompatActivity {
    private TimePicker timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        timePicker = (TimePicker) findViewById(R.id.activity_new_alarm_timePicker);

        timePicker.setIs24HourView(DateFormat.is24HourFormat(this));

        findViewById(R.id.activity_new_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newAlarm();
            }
        });
    }

    private void newAlarm(){
        //ConnectionSource connectionSource = new AndroidConnectionSource(getDatabaseHelper());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Alarm alarm = new Alarm();

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());

        alarm.setTimeOfMeet(calendar.getTime());

        final EditText nameOfEvent = (EditText) findViewById(R.id.activity_new_alarm_editText);

        nameOfEvent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length() > 0){
                    nameOfEvent.setVisibility(View.VISIBLE);
                }else{
                    nameOfEvent.setVisibility(View.INVISIBLE);
                }
            }
        });

        alarm.setNameOfEventCustom(nameOfEvent.getText().toString());
        alarm.setNameOfEventInCalendar(nameOfEvent.getText().toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        org.joda.time.DateTime dateTimeExtra = new org.joda.time.DateTime(simpleDateFormat.parse(prefs.getString("extraTime", "00:30"), new ParsePosition(0)));
        org.joda.time.DateTime datetime = new org.joda.time.DateTime(calendar.getTime());
        alarm.setExtraTime(dateTimeExtra.toDate());
        datetime = datetime.minus(Math.abs(dateTimeExtra.getMillis()));
        alarm.setTimeOfAlarm(datetime.toDate());
        alarm.setTypeOfRelocating(prefs.getString("transport", "0") == "1" ? 1 : 0);
        alarm.setSwitchOn(true);

        DBOperations.newAlarm(NewAlarmActivity.this, alarm);

        Intent intent = new Intent(NewAlarmActivity.this, MainActivity.class);
        startActivity(intent);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(NewAlarmActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
