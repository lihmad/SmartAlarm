package com.lamadesign.smartalarm.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

/**
 * Created by Adam on 27.08.2016.
 */
public class CalculateDistance {
    public static DistanceMatrix calculate(GeoApiContext context,Context apContext,TravelMode travelMode, com.google.maps.model.LatLng[] origins, com.google.maps.model.LatLng[] destinations){

        try {
            DistanceMatrix distanceMatrix = DistanceMatrixApi.newRequest(context)
                    .origins(origins)
                    .destinations(destinations)
                    .mode(travelMode)
                    .await();
            return distanceMatrix;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
