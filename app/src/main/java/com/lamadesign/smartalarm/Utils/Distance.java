package com.lamadesign.smartalarm.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.api.services.calendar.model.Event;
import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.TravelMode;
import com.lamadesign.smartalarm.Models.Alarm;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by Adam on 12.08.2016.
 */
public class Distance {
    public static Alarm getData(Alarm alarm, Event event, Context apContext, Location origin, DateTime dateTimeExtra){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(apContext);
        alarm.setPlaceOfMeet(event.getLocation());
        GeoApiContext context = GoogleContexts.context;
        try {
            GeocodingResult[] results = GeocodingApi.geocode(context, alarm.getPlaceOfMeet()).await();
            com.google.maps.model.LatLng l = results[0].geometry.location;
            alarm.setLatitude((long) l.lat);
            alarm.setLongtitude((long) l.lng);
            alarm.setTimeOfMeet(new Date(event.getStart().getDateTime().getValue()));
            //origin = LocationServices.FusedLocationApi.getLastLocation(
            //        mGoogleApiClient);
            com.google.maps.model.LatLng[] origins = {new com.google.maps.model.LatLng(origin.getLatitude(), origin.getLongitude())};
            com.google.maps.model.LatLng[] destinations = {l};
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                    .origins(origins)
                    .destinations(destinations)
                    .mode(prefs.getString("transport", "0") == "1" ? TravelMode.DRIVING : TravelMode.WALKING)
                    .await();
            DistanceMatrixRow distanceMatrixRow = distanceMatrix.rows[0];
            com.google.maps.model.Duration duration = distanceMatrixRow.elements[0].duration;
            //long time = alarmFromCalendar.timeOfMeet.getSeconds() - duration.inSeconds;
            org.joda.time.DateTime dateTime = new org.joda.time.DateTime(event.getStart().getDateTime().getValue());
            //dateTime = dateTime.minusSeconds((int) duration.inSeconds);
            dateTime = dateTime.minus(duration.inSeconds * 1000L);
            dateTime = dateTime.minus(Math.abs(dateTimeExtra.getMillis()));
            alarm.setTimeOfAlarm(dateTime.toDate());
            return alarm;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
