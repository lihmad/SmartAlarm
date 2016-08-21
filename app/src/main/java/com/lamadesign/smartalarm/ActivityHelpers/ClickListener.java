package com.lamadesign.smartalarm.ActivityHelpers;

import android.view.View;

/**
 * Created by Adam on 21.08.2016.
 */
public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
