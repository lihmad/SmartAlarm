package com.lamadesign.smartalarm.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.api.services.calendar.model.Event;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixRow;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
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
            alarm.setLongitude((long) l.lng);
            //origin = LocationServices.FusedLocationApi.getLastLocation(
            //        mGoogleApiClient);
            com.google.maps.model.LatLng[] origins = {new com.google.maps.model.LatLng(origin.getLatitude(), origin.getLongitude())};
            com.google.maps.model.LatLng[] destinations = {l};
            TravelMode travelMode = prefs.getString("transport", "0") == "1" ? TravelMode.DRIVING : TravelMode.WALKING;
            DistanceMatrix distanceMatrix = CalculateDistance.calculate(context, apContext, travelMode, origins, destinations);
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

    public static Void saveData(android.location.Location origin, Context apContext, Alarm alarm){

        try {
            GeoApiContext context =  GoogleContexts.context;


            LatLng[] origins = {new LatLng(origin.getLatitude(), origin.getLongitude())};
            GeocodingResult[] results;
            if(alarm.getPlaceOfMeet().length() == 0){
                results = GeocodingApi.reverseGeocode(context, origins[0]).await();
            }else{
                results = GeocodingApi.geocode(context, alarm.getPlaceOfMeet()).await();
            }


            com.google.maps.model.LatLng l = results[0].geometry.location;
            alarm.setLatitude(l.lat);
            alarm.setLongitude(l.lng);
            LatLng[] destinations = {new LatLng(l.lat, l.lng)};
            //com.google.maps.model.LatLng[] origins = {new com.google.maps.model.LatLng(latLng[1].latitude, latLng[1].longitude)};

            TravelMode travelMode = alarm.getTypeOfRelocating() == 1 ? TravelMode.DRIVING : TravelMode.WALKING;
            DistanceMatrix distanceMatrix = CalculateDistance.calculate(context, apContext, travelMode, origins, destinations);
            DistanceMatrixRow distanceMatrixRow = distanceMatrix.rows[0];
            com.google.maps.model.Duration duration = distanceMatrixRow.elements[0].duration;
            //long time = alarmFromCalendar.timeOfMeet.getSeconds() - duration.inSeconds;

            //TODO tohle asi dat nekam bokem
            DateTime dateTime = new DateTime(alarm.getTimeOfMeet().getTime());
            dateTime = dateTime.minus(duration.inSeconds*1000L);
            dateTime = dateTime.minus(Math.abs(alarm.getExtraTime().getTime()));
            alarm.setTimeOfAlarm(dateTime.toDate());
            DBOperations.updateAlarm(apContext, alarm);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getDataWithReverseGeocode(Alarm alarm,Context apContext, com.google.android.gms.maps.model.LatLng... latLng){
        GeoApiContext context = GoogleContexts.context;
        try {
            com.google.maps.model.LatLng l = new com.google.maps.model.LatLng(latLng[0].latitude, latLng[0].longitude);
            GeocodingResult[] results = GeocodingApi.reverseGeocode(context, l).await();
            alarm.setPlaceOfMeet(results[0].formattedAddress);
            com.google.maps.model.LatLng[] origins = {new com.google.maps.model.LatLng(latLng[1].latitude, latLng[1].longitude)};
            com.google.maps.model.LatLng[] destinations = {l};
            TravelMode travelMode = alarm
                    .getTypeOfRelocating() == 1 ? TravelMode.DRIVING : TravelMode.WALKING;
            DistanceMatrix distanceMatrix = CalculateDistance.calculate(context, apContext, travelMode, origins, destinations);
            DistanceMatrixRow distanceMatrixRow = distanceMatrix.rows[0];
            com.google.maps.model.Duration duration = distanceMatrixRow.elements[0].duration;
            //long time = alarmFromCalendar.timeOfMeet.getSeconds() - duration.inSeconds;
            DateTime dateTime = new DateTime(alarm.timeOfMeet.getTime());
            dateTime = dateTime.minus(duration.inSeconds*1000L);
            dateTime = dateTime.minus(Math.abs(alarm.getExtraTime().getTime()));
            alarm.setTimeOfAlarm(dateTime.toDate());
            DBOperations.updateAlarm(apContext, alarm);
            return results[0].formattedAddress;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
