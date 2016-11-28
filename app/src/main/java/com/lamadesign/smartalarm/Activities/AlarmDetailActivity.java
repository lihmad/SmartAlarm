package com.lamadesign.smartalarm.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.lamadesign.smartalarm.ActivityHelpers.PlacesAutocompleteAdapter;
import com.lamadesign.smartalarm.Fragments.MapActivity;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.Models.CalculateAlarmWrapper;
import com.lamadesign.smartalarm.Models.CalculateAndSaveWrapper;
import com.lamadesign.smartalarm.R;
import com.lamadesign.smartalarm.Utils.CalculateAlarmAsync;
import com.lamadesign.smartalarm.Utils.DBOperations;
import com.lamadesign.smartalarm.Utils.SaveAndComputeAlarmAsync;

import org.joda.time.DateTime;

import java.util.Date;
import java.util.concurrent.ExecutionException;

public class AlarmDetailActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private long id;
    private Alarm alarm;
    private AutoCompleteTextView place;
    private RadioGroup radioGroup;
    private android.location.Location origin;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Intent getIntent = getIntent();
        id = getIntent.getLongExtra("id", -1);

        final EditText nameOfEvent = (EditText) findViewById(R.id.activity_alarm_detail_editText_eventName);
        place = (AutoCompleteTextView) findViewById(R.id.activity_alarm_detail_editText_place);
        radioGroup = (RadioGroup) findViewById(R.id.activity_alarm_detail_radioGroup);

        Button map = (Button) findViewById(R.id.activity_alarm_detail_button_find);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AlarmDetailActivity.this, MapActivity.class);
                i.putExtra("id", id);
                startActivityForResult(i, 1);
                //startActivity(i);
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this).build();

        alarm = DBOperations.getAlarm(AlarmDetailActivity.this, id);
        radioGroup.check(alarm.getTypeOfRelocating() == 0 ? R.id.activity_alarm_detail_radioButton_walking : R.id.activity_alarm_detail_radioButton_driving);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                alarm.setTypeOfRelocating(checkedId == R.id.activity_alarm_detail_radioButton_walking ? 0 : 1);
                DBOperations.updateAlarm(AlarmDetailActivity.this, alarm);
            }
        });

        nameOfEvent.setText(alarm.nameOfEventCustom);

        place.setText((alarm.placeOfMeet != null || alarm.placeOfMeet != "") ? alarm.placeOfMeet : null);

        PlacesAutocompleteAdapter placesAutocompleteAdapter = new PlacesAutocompleteAdapter(this, R.layout.list_item);
        placesAutocompleteAdapter.setGoogleApiClient(googleApiClient);
        place.setAdapter(placesAutocompleteAdapter);
        place.setThreshold(3);

        //TODO: tohle asi nepotřebuju
        place.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int position, long id) {
                //String str = (String) adapterView.getItemAtPosition(position);

                //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            }
        });

        final TimePicker timePicker = (TimePicker) findViewById(R.id.activity_alarm_detail_timePicker);

        timePicker.setIs24HourView(DateFormat.is24HourFormat(AlarmDetailActivity.this));

        if(alarm.getCalendarID() == null){
            Date date = alarm.getTimeOfMeet();
            DateTime dateTime = new DateTime(date.getTime());
            //TODO: tohle by chtělo předělat nějak aby nebylo deprecated api, setHour() ma ale min API 23...
            timePicker.setCurrentHour(dateTime.getHourOfDay());
            timePicker.setCurrentMinute(dateTime.getMinuteOfHour());
        }else{
            timePicker.setCurrentHour(0);
            timePicker.setCurrentMinute(30);
        }

        Button save = (Button) findViewById(R.id.activity_alarm_detail_button_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameOfEvent.getText() != null && nameOfEvent.getText().toString().trim() != "" )
                    alarm.setNameOfEventCustom(nameOfEvent.getText().toString().trim());
                if (place.getText() != null &&  place.getText().toString().trim() != ""){

                    alarm.setPlaceOfMeet(place.getText().toString().trim());

                    if(alarm.getCalendarID() == null){
                        Date date = new Date(System.currentTimeMillis());
                        DateTime dateTime = new DateTime((1900 + date.getYear()), date.getMonth() + 1, date.getDate(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());

                        alarm.setTimeOfMeet(dateTime.toDate());

                    }else{
                        DateTime dateTime = new DateTime(1970, 1, 1, timePicker.getCurrentHour(), timePicker.getCurrentMinute(),0);
                        alarm.setExtraTime(dateTime.toDate());

                    }

                    try {
                        new SaveAndComputeAlarmAsync().execute(new CalculateAndSaveWrapper(alarm, AlarmDetailActivity.this, origin)).get();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                DBOperations.updateAlarm(AlarmDetailActivity.this, alarm);
                Intent intent = new Intent(AlarmDetailActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        alarm = DBOperations.getAlarm(this, id);
        place.setText(alarm.getPlaceOfMeet());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                ProgressBar progressBar = (ProgressBar)findViewById(R.id.activity_alarm_detail_progressBar);
                progressBar.setIndeterminate(true);
                progressBar.setVisibility(View.VISIBLE);
                double[] latLngs =data.getDoubleArrayExtra("latLng");
                com.google.android.gms.maps.model.LatLng lL = new com.google.android.gms.maps.model.LatLng(latLngs[0], latLngs[1]);
                com.google.android.gms.maps.model.LatLng lLO = new com.google.android.gms.maps.model.LatLng(latLngs[2], latLngs[3]);
                String placeOfMeet = "";
                try {
                    placeOfMeet = new CalculateAlarmAsync().execute(new CalculateAlarmWrapper(alarm, AlarmDetailActivity.this,new com.google.android.gms.maps.model.LatLng[]{lL, lLO} )).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                place.setText(placeOfMeet);
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        origin = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AlarmDetailActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
