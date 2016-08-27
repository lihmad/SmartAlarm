package com.lamadesign.smartalarm.Utils;

import android.os.AsyncTask;

import com.lamadesign.smartalarm.Models.CalculateAndSaveWrapper;

/**
 * Created by Adam on 27.08.2016.
 */
public class SaveAndComputeAlarmAsync extends AsyncTask<CalculateAndSaveWrapper, Void, Void> {
    @Override
    protected Void doInBackground(CalculateAndSaveWrapper... calculateAndSaveWrappers) {
        CalculateAndSaveWrapper wrapper = calculateAndSaveWrappers[0];
        Distance.saveData(wrapper.origin, wrapper.context, wrapper.alarm);
        return null;
    }
}
