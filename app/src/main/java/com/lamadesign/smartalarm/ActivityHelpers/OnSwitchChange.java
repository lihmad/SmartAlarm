package com.lamadesign.smartalarm.ActivityHelpers;

import android.widget.CompoundButton;

import com.lamadesign.smartalarm.Models.Alarm;

/**
 * Created by Adam on 07.08.2016.
 */
public class OnSwitchChange implements CompoundButton.OnCheckedChangeListener {
    private int position;
    private Alarm alarm;
    private OnItemSwitchCallback onItemSwitchCallback;
    public OnSwitchChange(Alarm alarm, OnItemSwitchCallback onItemSwitchCallback){
        this.alarm = alarm;
        this.onItemSwitchCallback = onItemSwitchCallback;

    }
    public interface OnItemSwitchCallback {
        void onItemSwitched(CompoundButton compoundButton, Alarm alarm, boolean isChecked);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        onItemSwitchCallback.onItemSwitched(compoundButton, alarm, isChecked);
    }
}
