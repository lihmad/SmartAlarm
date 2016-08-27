package com.lamadesign.smartalarm.Models;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by Adam on 26.08.2016.
 */
public class PlacesAutocompleteParametersModel {

    public GoogleApiClient googleApiClient;
    public String request;

    public PlacesAutocompleteParametersModel(){}

    public PlacesAutocompleteParametersModel(GoogleApiClient googleApiClient, String request) {
        this.googleApiClient = googleApiClient;
        this.request = request;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    @Override
    public String toString() {
        return "PlacesAutocompleteParametersModel{" +
                "googleApiClient=" + googleApiClient +
                ", request='" + request + '\'' +
                '}';
    }
}
