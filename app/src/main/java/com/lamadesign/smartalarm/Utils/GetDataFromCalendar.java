package com.lamadesign.smartalarm.Utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.Models.LocationContextWrapper;
import com.lamadesign.smartalarm.Services.PeriodicAlarmReceiver;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adam on 12.08.2016.
 */
public class GetDataFromCalendar extends AsyncTask<LocationContextWrapper, Void, Context> {

    private com.google.api.services.calendar.Calendar mService = null;
    private Exception mLastError = null;


    public GetDataFromCalendar(GoogleAccountCredential credential) {

        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API")
                .build();
    }

    /**
     * Fetch a list of the next 10 events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private Context getDataFromApi(Context context, Location origin) throws IOException {
        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        List<String> eventStrings = new ArrayList<String>();
        Events events = mService.events().list("primary")
                .setMaxResults(50)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();
        List<Event> items = events.getItems();


        List<Alarm> alarms = new ArrayList<Alarm>();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        for (Event event : items) {
            Alarm alarm = new Alarm();
            alarm.setNameOfEventCustom(event.getSummary());
            alarm.setNameOfEventInCalendar(event.getSummary());
            Date date = new Date();
            date.setTime(event.getStart().getDate() == null ? event.getStart().getDateTime().getValue() : event.getStart().getDate().getValue());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            org.joda.time.DateTime dateTimeExtra = new org.joda.time.DateTime(simpleDateFormat.parse(prefs.getString("extraTime", "00:30"), new ParsePosition(0)));
            org.joda.time.DateTime datetime = new org.joda.time.DateTime(date.getTime());
            alarm.setExtraTime(dateTimeExtra.toDate());
            if(event.getLocation() != null){
                Distance.getData(alarm,event,context, origin, dateTimeExtra);
            }else{
                datetime = datetime.minus(Math.abs(dateTimeExtra.getMillis()));
                alarm.setTimeOfAlarm(datetime.toDate());
            }
            alarm.setTypeOfRelocating(prefs.getString("transport", "0") == "1" ? 1 : 0);
            alarm.setSwitchOn(true);
            alarm.setTimeOfMeet(date);
            alarm.setCalendarID(event.getId());

            alarms.add(alarm);
        }
        for (Alarm alarm : alarms){
            DBOperations.setCalendarDB(alarm,context);

        }


        return context;
    }

    @Override
    protected void onPostExecute(Context context) {

        Intent intent = new Intent(context, PeriodicAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(context, PeriodicAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //long firstMillis = System.currentTimeMillis(); // alarm is set right away
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.setExact(AlarmManager.RTC, DBOperations.getAlarmToWakeUp(context).getTimeOfAlarm().getTime(), pIntent);

        //alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
        //AlarmManager.INTERVAL_FIFTEEN_MINUTES, pIntent);
//        if (output == null || output.size() == 0) {
//            //mOutputText.setText("No results returned.");
//        } else {
//            //output.add(0, "Data retrieved using the Google Calendar API:");
//            //mOutputText.setText(TextUtils.join("\n", output));
//        }
    }

    @Override
    protected Context doInBackground(LocationContextWrapper... locationContextWrappers) {
        try {
            return getDataFromApi(locationContextWrappers[0].context, locationContextWrappers[0].location);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }
}
