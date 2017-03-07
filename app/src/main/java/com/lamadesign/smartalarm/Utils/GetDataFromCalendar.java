package com.lamadesign.smartalarm.Utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.lamadesign.smartalarm.Database.DBOperations;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.Models.LocationContextWrapper;
import com.lamadesign.smartalarm.Services.PeriodicAlarmReceiver;

import org.apache.commons.lang3.StringUtils;

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
     * Fetch a list of the events from the primary calendar.
     * @return List of Strings describing returned events.
     * @throws IOException
     */
    private Context getDataFromApi(Context context, Location origin) throws IOException {
        DateTime now = new DateTime(System.currentTimeMillis());
        //List<String> eventStrings = new ArrayList<String>();
        List<Event> items = null;

        try {
            Events events = mService.events().list("primary")
                    .setMaxResults(50)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            items = events.getItems();
        }catch(Exception e){
            Log.w("Chyba", e.getMessage());

        }



        //TODO tohle zpracovani nejlepe nekde jinde

        //TODO prepsat to tak aby jsem ulozil novy alarmy a sikovne updatoval ty ktery existujou
        List<Alarm> alarmsNew = new ArrayList<Alarm>();
        List<Alarm> alarmsUpdated = new ArrayList<>();

        List<String> googleids = new ArrayList<>();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //Extra cas navic je stejny pro vsechny udalosti
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        org.joda.time.DateTime dateTimeExtra = new org.joda.time.DateTime(simpleDateFormat.parse(prefs.getString("extraTime", "00:30"), new ParsePosition(0)));
        for (Event event : items) {
            String eventId = event.getId();
            googleids.add(eventId);
            Alarm alarm;
            alarm = DBOperations.getAlarmByGoogleId(context, eventId);
            if(alarm == null){
                alarm = new Alarm();
                alarm.setNameOfEventCustom(event.getSummary());
                alarm.setNameOfEventInCalendar(event.getSummary());
                Date date = new Date();
                //vytahnu datum bud z getDateTime nebo z getDate
                date.setTime(event.getStart().getDateTime() != null ? event.getStart().getDateTime().getValue() : event.getStart().getDate().getValue());
                org.joda.time.DateTime datetime = new org.joda.time.DateTime(date.getTime());
                alarm.setExtraTime(dateTimeExtra.toDate());
                if(event.getLocation() != null){
                    //pokud je dostupne misto tak si ho ulozim
                    Distance.getData(alarm,event,context, origin, dateTimeExtra);
                }else{
                    datetime = datetime.minus(Math.abs(dateTimeExtra.getMillis()));
                    alarm.setTimeOfAlarm(datetime.toDate());
                }
                alarm.setTypeOfRelocating(prefs.getString("transport", "0").equals("1") ? 1 : 0);
                alarm.setSwitchOn(true);
                alarm.setTimeOfMeet(date);
                alarm.setTimeOfMeetInCalendar(date);
                alarm.setCalendarID(event.getId());

                alarmsNew.add(alarm);
            }else{
                //TODO uppozornit uzivatele ze se zmenili data a kdyztak prepsat? mozna nejaka bublina s cislem u kazde udalosti signalizujici pocet zmen
                if(alarm.getNameOfEventCustom().toString().equalsIgnoreCase(alarm.getNameOfEventInCalendar().toString())){
                    //pokud se mi rovnaji jmeno potom ho uzivatel nezmenil a muzu prepsat
                    if(!event.getSummary().toString().equalsIgnoreCase(alarm.getNameOfEventInCalendar().toString())){
                        alarm.setNameOfEventCustom(event.getSummary());
                        alarm.setNameOfEventInCalendar(event.getSummary());
                    }
                }else{
                    if(!event.getSummary().toString().equalsIgnoreCase(alarm.getNameOfEventInCalendar().toString())){
                        alarm.setNameOfEventInCalendar(event.getSummary());
                    }
                }

                if(!StringUtils.isBlank(alarm.getPlaceOfMeet()) && StringUtils.isBlank(alarm.getPlaceOfMeetInCalendar())){
                    if (alarm.getPlaceOfMeet().toString().equalsIgnoreCase(alarm.getPlaceOfMeetInCalendar().toString())) {
                        if (!event.getLocation().toString().equalsIgnoreCase(alarm.getPlaceOfMeetInCalendar().toString())) {
                            org.joda.time.DateTime extraTime = new org.joda.time.DateTime(alarm.getExtraTime());
                            Distance.getData(alarm, event, context, origin, extraTime);
                            alarm.setplaceOfMeetInCalendar(event.getLocation());
                        }
                    }
                }else if(!StringUtils.isBlank(event.getLocation()) && !StringUtils.isBlank(alarm.getPlaceOfMeetInCalendar())){
                    if(!event.getLocation().toString().equalsIgnoreCase(alarm.getPlaceOfMeetInCalendar().toString())){
                        alarm.setplaceOfMeetInCalendar(event.getLocation());
                    }
                }

                Date date = new Date();
                //vytahnu datum bud z getDateTime nebo z getDate
                date.setTime(event.getStart().getDateTime() != null ? event.getStart().getDateTime().getValue() : event.getStart().getDate().getValue());

                if(alarm.getTimeOfMeet().equals(alarm.getTimeOfMeetInCalendar())){
                    if(!date.equals(alarm.getTimeOfMeetInCalendar())){
                        alarm.setTimeOfMeet(date);
                        alarm.setTimeOfMeetInCalendar(date);
                    }

                }else{
                    if(!date.equals(alarm.getTimeOfMeetInCalendar())){
                        alarm.setTimeOfMeetInCalendar(date);
                    }
                }

                alarmsUpdated.add(alarm);
            }

        }
        if(!alarmsNew.isEmpty())
            DBOperations.addNewAlarms(alarmsNew, context);
        if(!alarmsUpdated.isEmpty())
            DBOperations.updateAlarms(alarmsUpdated, context);


        //vycistit smazane udalosti
        Event event = items.get(items.size() - 1);//vezmu si posledni event kvuli casu
        Date date = new Date();
        //vytahnu datum bud z getDateTime nebo z getDate
        date.setTime(event.getStart().getDateTime() != null ? event.getStart().getDateTime().getValue() : event.getStart().getDate().getValue());
        List<Alarm> alarmsToDelete = DBOperations.getAlarmsBeforeTimeForDelete(context, date, googleids);
        if(!alarmsToDelete.isEmpty())
            DBOperations.bulkDelete(context, alarmsToDelete);


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
