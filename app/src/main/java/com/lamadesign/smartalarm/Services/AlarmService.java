package com.lamadesign.smartalarm.Services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.lamadesign.smartalarm.Database.DBOperations;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.Models.LocationContextWrapper;
import com.lamadesign.smartalarm.Utils.GetDataFromCalendar;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AlarmService extends IntentService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleAccountCredential mCredential;

    static final int REQUEST_ACCOUNT_PICKER = 1000;//
    static final int REQUEST_AUTHORIZATION = 1001;//
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;//
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;//

    private static final String PREF_ACCOUNT_NAME = "accountName";//
    private static final String[] SCOPES = {CalendarScopes.CALENDAR_READONLY};//

    protected GoogleApiClient mGoogleApiClient;
    private Location origin;

    public AlarmService() {
        super("AlarmService");
    }

    //TODO: kam jdu do onHandleIntent nebo do onStartCommand???
    @Override
    protected void onHandleIntent(Intent intent) {
        //TODO: dostanu se vubec do try catche??
        try {
            if (intent.getStringExtra("Wakefull") == "Wakefull") {
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
            }
        } catch (Exception e) {
        }
        long ct = System.currentTimeMillis();
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), AlarmService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 5, i, 0);

        mgr.set(AlarmManager.RTC_WAKEUP, ct + 60000, pi);
        //stopSelf();


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (intent != null && intent.getStringExtra("Wakefull") == "Wakefull") {
                WakefulBroadcastReceiver.completeWakefulIntent(intent);
            }
        } catch (Exception e) {
        }

        long ct = System.currentTimeMillis();
        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getApplicationContext(), AlarmService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(), 5, i, 0);

        mgr.set(AlarmManager.RTC_WAKEUP, ct + 60000, pi);
        //stopSelf();
        Log.i("AlarmService", "jedu");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();

        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        origin = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        getCalendarData();
    }

    private void getCalendarData() {
        mCredential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());


        getResultsFromApi();

        ////////////////
        try{
            Alarm a = DBOperations.getAlarmToWakeUp(this.getApplicationContext());

            Date s = a.getTimeOfAlarm();
            Date n = new Date();
            if(s.getYear() == n.getYear() && s.getMonth() == n.getMonth() && s.getDate() == n.getDate()){
                Intent alarmIntent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, alarmIntent, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, s.getHours());
                calendar.set(Calendar.MINUTE, s.getMinutes());

                //alarmManager.setInexactRepeating(alarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                alarmManager.setExact(alarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            }

        }catch(Exception e){

        }



    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else {
            LocationContextWrapper wrapper = new LocationContextWrapper(getApplicationContext(), origin);
            new GetDataFromCalendar(mCredential).execute(wrapper);
            //new MakeRequestTask(mCredential).execute();
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            SharedPreferences preferences = getSharedPreferences("account", Context.MODE_PRIVATE);
            String accountName = preferences.getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                LocationContextWrapper wrapper = new LocationContextWrapper(getApplicationContext(), origin);
                new GetDataFromCalendar(mCredential).execute(wrapper);
            } else {
                //uzivatel nema zadanej account nic se neda deleat az zada budeme fungovat
            }
        } else {
            //a pokud nemam povoleni sahat na ucty tak se take nic neda delat
        }
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
    }

    @Override
    public void onDestroy() {
        mGoogleApiClient.disconnect();
        //TODO: je ten sendBroadcast tady k necemu??
        sendBroadcast(new Intent("YouWillNeverKillMe"));
        super.onDestroy();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
