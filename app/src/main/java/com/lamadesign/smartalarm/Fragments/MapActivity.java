package com.lamadesign.smartalarm.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.R;
import com.lamadesign.smartalarm.Utils.DBOperations;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap map;
    private long id;
    private LatLng latLng;
    private Location origin;
    private Alarm alarm;
    private boolean fromCalendar = false;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent getIntent = getIntent();
        id = getIntent.getLongExtra("id", -1);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
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

        double[] lL = new double[]{latLng.latitude, latLng.longitude, origin.getLatitude(), origin.getLongitude()};
        Intent intent = new Intent();
        intent.putExtra("latLng", lL);
        setResult(RESULT_OK, intent);
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        alarm = DBOperations.getAlarm(MapActivity.this, id);

        LatLng alarmLatLng;
        if (alarm.getPlaceOfMeet() == null || Double.isNaN(alarm.getLatitude()) || Double.isNaN(alarm.getLongtitude())) {

        } else {
            fromCalendar = true;
            origin = new Location("");
            origin.setLatitude(alarm.getLongtitude());
            origin.setLongitude(alarm.getLatitude());
            alarmLatLng = new LatLng(alarm.getLatitude(), alarm.getLongtitude());
            map.addMarker(new MarkerOptions()
                    .position(alarmLatLng)
                    .title("Zde se nachází Vaše schůzka " + alarm.getNameOfEventCustom())
                    .draggable(true));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(alarmLatLng, 14.0f));
            latLng = alarmLatLng;
        }

        //map.moveCamera(CameraUpdateFactory.newLatLng(alarm));
        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(alarm, 14.0f));

        map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latLng = marker.getPosition();
                alarm.setLongtitude(latLng.longitude);
                alarm.setLatitude(latLng.latitude);
            }
        });


    }

    @Override
    public void onConnected(Bundle bundle) {
        if(fromCalendar)
            return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        origin = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LatLng alarmLatLng = new LatLng(origin.getLatitude(), origin.getLongitude());
        map.addMarker(new MarkerOptions()
                .position(alarmLatLng)
                .title("Zde se nacházíte, přesuňte bod, kde se bude odehrávat Vaše schůzka " + alarm.getNameOfEventCustom())
                .draggable(true));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(alarmLatLng, 14.0f));
        latLng = alarmLatLng;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
