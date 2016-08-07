package com.lamadesign.smartalarm.ActivityHelpers;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private OnSwitchChange.OnItemSwitchCallback onItemSwitchCallback;

    public AlarmAdapter(List<Alarm> alermList, OnSwitchChange.OnItemSwitchCallback onItemSwitchCallback){
        this.alarmList = alermList;
        this.onItemSwitchCallback = onItemSwitchCallback;
    }

    public class AlarmViewHolder extends RecyclerView.ViewHolder {
        public TextView time, date, name, place;
        public Switch onoff;

        public AlarmViewHolder(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.alarm_list_row_time_of_event);
            date = (TextView) view.findViewById(R.id.alarm_list_row_date);
            name = (TextView) view.findViewById(R.id.alarm_list_row_name);
            place = (TextView) view.findViewById(R.id.alarm_list_row_place);
            onoff = (Switch) view.findViewById(R.id.alarm_list_row_switch);
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
        Alarm alarmFromCalendar = alarmList.get(position);
        DateTime dateTime = new DateTime(alarmFromCalendar.getTimeOfMeet().getTime());
        if(alarmFromCalendar.getTimeOfAlarm() == null){
            dateTimeAlarm = new DateTime(dateTime);
            dateTimeAlarm = dateTimeAlarm.minusMinutes(30);

        }else{
            dateTimeAlarm = new DateTime(alarmFromCalendar.getTimeOfAlarm().getTime());
        }
        holder.time.setText(dateTimeAlarm.toString("dd. MM. yyyy"));
        holder.date.setText(dateTimeAlarm.toLocalTime().toString("HH:mm"));
        holder.name.setText(alarmFromCalendar.getNameOfEventCustom());
        holder.place.setText(alarmFromCalendar.getPlaceOfMeet());
        holder.onoff.setChecked(alarmFromCalendar.isSwitchOn());
        holder.onoff.setOnCheckedChangeListener(new OnSwitchChange(alarmList.get(position), onItemSwitchCallback));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }
}
