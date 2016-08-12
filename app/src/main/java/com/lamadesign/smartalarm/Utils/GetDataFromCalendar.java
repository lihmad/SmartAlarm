package com.lamadesign.smartalarm.Utils;


import android.content.Context;
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

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Adam on 12.08.2016.
 */
public class GetDataFromCalendar extends AsyncTask<LocationContextWrapper, Void, List<String>> {

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
    private List<String> getDataFromApi(Context context, Location origin) throws IOException {
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


        return eventStrings;
    }

    @Override
    protected List<String> doInBackground(LocationContextWrapper... locationContextWrappers) {
        try {
            return getDataFromApi(locationContextWrappers[0].context, locationContextWrappers[0].location);
        } catch (Exception e) {
            mLastError = e;
            cancel(true);
            return null;
        }
    }
}
