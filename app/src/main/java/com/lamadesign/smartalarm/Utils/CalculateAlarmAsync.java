package com.lamadesign.smartalarm.Utils;

import android.os.AsyncTask;

import com.lamadesign.smartalarm.Models.CalculateAlarmWrapper;

/**
 * Created by Adam on 27.08.2016.
 */
public class CalculateAlarmAsync extends AsyncTask<CalculateAlarmWrapper,Void,String> {
    @Override
    protected String doInBackground(CalculateAlarmWrapper... calculateAlarmWrappers) {
        CalculateAlarmWrapper wrapper = calculateAlarmWrappers[0];
        if(wrapper.latLng[0] != null){
            return Distance.getDataWithReverseGeocode(wrapper.alarm, wrapper.context, wrapper.latLng);

        }
        return null;
    }
}
