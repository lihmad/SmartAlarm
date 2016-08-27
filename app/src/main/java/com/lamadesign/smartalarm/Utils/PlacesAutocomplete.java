package com.lamadesign.smartalarm.Utils;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;
import com.lamadesign.smartalarm.Models.PlacesAutocompleteParametersModel;

import java.util.ArrayList;

/**
 * Created by Adam on 26.08.2016.
 */
public class PlacesAutocomplete extends AsyncTask<PlacesAutocompleteParametersModel, Void, ArrayList<String>> {
    @Override
    protected ArrayList<String> doInBackground(PlacesAutocompleteParametersModel... parametersModel) {
        final ArrayList<String> places = new ArrayList<>();

        final AutocompleteFilter.Builder autocompleteFilter = new AutocompleteFilter.Builder();
        autocompleteFilter.setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE);

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        com.google.android.gms.maps.model.LatLng latLng = new com.google.android.gms.maps.model.LatLng(0, 0);
        bounds.include(latLng);
        PendingResult<AutocompletePredictionBuffer> result =
                Places.GeoDataApi.getAutocompletePredictions(parametersModel[0].googleApiClient, parametersModel[0].request,
                        bounds.build(), autocompleteFilter.build());

        AutocompletePredictionBuffer autocompletePredictionBuffer = result.await();
        for (AutocompletePrediction prediction : autocompletePredictionBuffer){
            places.add(prediction.getFullText(null).toString());
        }

        return places;

    }
}
