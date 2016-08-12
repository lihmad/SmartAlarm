package com.lamadesign.smartalarm.Models;

import android.content.Context;
import android.location.Location;

/**
 * Created by Adam on 12.08.2016.
 */
public class LocationContextWrapper {
    public Context context;
    public Location location;

    public LocationContextWrapper(Context context, Location location){
        this.location = location;
        this.context = context;
    }
}
