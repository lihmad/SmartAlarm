package com.lamadesign.smartalarm.ActivityHelpers;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.R;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Adam on 07.08.2016.
 */
public class AlarmAdapter  extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>{
    private List<Alarm> alarmList;

    public AlarmAdapter(List<Alarm> alarmList){
        this.alarmList = alarmList;
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        public TextView time, date, name, place;
        public ImageView turnIndicator;

        public AlarmViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.alarm_list_row_time_of_event);
            date = (TextView) view.findViewById(R.id.alarm_list_row_date);
            name = (TextView) view.findViewById(R.id.alarm_list_row_name);
            place = (TextView) view.findViewById(R.id.alarm_list_row_place);
            turnIndicator = (ImageView) view.findViewById(R.id.alarm_list_row_imageView);
        }
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_list_row, parent, false);
        //ViewHolder viewHolder = new ViewHolder(v);
        //return viewHolder;
        return new AlarmViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int position) {
        //holder.textView.setText(dataset[position]);
        holder.setIsRecyclable(false);
        DateTime dateTimeAlarm = null;
        Alarm alarm = alarmList.get(position);
        DateTime dateTime = new DateTime(alarm.getTimeOfMeet().getTime());
        if(alarm.getTimeOfAlarm() == null){
            dateTimeAlarm = new DateTime(dateTime);
            dateTimeAlarm = dateTimeAlarm.minusMinutes(30);

        }else{
            dateTimeAlarm = new DateTime(alarm.getTimeOfAlarm().getTime());
        }
        holder.time.setText(dateTimeAlarm.toString("dd. MM. yyyy"));
        holder.date.setText(dateTimeAlarm.toLocalTime().toString("HH:mm"));
        holder.name.setText(alarm.getNameOfEventCustom());
        holder.place.setText(alarm.getPlaceOfMeet());
        //holder.onoff.setChecked(alarm.isSwitchOn());
        int color;
        if(alarm.isSwitchOn())
            color = Color.parseColor("#388E3C");
        else
            color = Color.parseColor("#D32F2F");
        holder.turnIndicator.setBackgroundColor(color);
        //holder.onoff.setOnCheckedChangeListener(new OnSwitchChange(alarmList.get(position), onItemSwitchCallback));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }
}
