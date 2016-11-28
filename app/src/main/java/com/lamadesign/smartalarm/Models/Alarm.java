package com.lamadesign.smartalarm.Models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Adam on 07.08.2016.
 */
@DatabaseTable
public class Alarm implements Serializable {

    private static final long serialVersionUID = -5448365387166286193L;

    @DatabaseField(generatedId = true)
    public long alarmID;

    @DatabaseField(unique = true)
    public String calendarID;

    @DatabaseField
    public Date timeOfMeet;

    @DatabaseField
    public Date timeOfMeetInCalendar;

    @DatabaseField
    public Date timeOfAlarm;

    @DatabaseField
    public String placeOfMeet;

    @DatabaseField
    public String placeOfMeetInCalendar;

    @DatabaseField
    public String nameOfEventInCalendar;

    @DatabaseField
    public String nameOfEventCustom;

    @DatabaseField
    public Date extraTime;

    @DatabaseField
    public int typeOfRelocating;

    @DatabaseField
    public double latitude;

    @DatabaseField
    public double longtitude;

    @DatabaseField
    public boolean switchOn;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public long getAlarmID() {
        return alarmID;
    }

    public void setAlarmID(long alarmID) {
        this.alarmID = alarmID;
    }

    public String getCalendarID() {
        return calendarID;
    }

    public void setCalendarID(String calendarID) {
        this.calendarID = calendarID;
    }

    public Date getTimeOfMeet() {
        return timeOfMeet;
    }

    public void setTimeOfMeet(Date timeOfMeet) {
        this.timeOfMeet = timeOfMeet;
    }

    public Date getTimeOfMeetInCalendar() { return timeOfMeetInCalendar; }

    public void setTimeOfMeetInCalendar(Date timeOfMeetInCalendar) {
        this.timeOfMeetInCalendar = timeOfMeetInCalendar;
    }

    public Date getTimeOfAlarm() {
        return timeOfAlarm;
    }

    public void setTimeOfAlarm(Date timeOfAlarm) {
        this.timeOfAlarm = timeOfAlarm;
    }

    public String getPlaceOfMeet() {
        return placeOfMeet;
    }

    public void setPlaceOfMeet(String placeOfMeet) {
        this.placeOfMeet = placeOfMeet;
    }

    public String getPlaceOfMeetInCalendar() {
        return placeOfMeetInCalendar;
    }

    public void setplaceOfMeetInCalendar(String placeOfMeetInCalendar) {
        this.placeOfMeetInCalendar = placeOfMeetInCalendar;
    }

    public String getNameOfEventInCalendar() {
        return nameOfEventInCalendar;
    }

    public void setNameOfEventInCalendar(String nameOfEventInCalendar) {
        this.nameOfEventInCalendar = nameOfEventInCalendar;
    }

    public String getNameOfEventCustom() {
        return nameOfEventCustom;
    }

    public void setNameOfEventCustom(String nameOfEventCustom) {
        this.nameOfEventCustom = nameOfEventCustom;
    }

    public Date getExtraTime() {
        return extraTime;
    }

    public void setExtraTime(Date extraTime) {
        this.extraTime = extraTime;
    }

    public int getTypeOfRelocating() {
        return typeOfRelocating;
    }

    public void setTypeOfRelocating(int typeOfRelocating) {
        this.typeOfRelocating = typeOfRelocating;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longtitude;
    }

    public void setLongitude(double longitude) {
        this.longtitude = longitude;
    }

    public boolean isSwitchOn() {
        return switchOn;
    }

    public void setSwitchOn(boolean switchOn) {
        this.switchOn = switchOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        if (alarmID != alarm.alarmID) return false;
        if (typeOfRelocating != alarm.typeOfRelocating) return false;
        if (Double.compare(alarm.latitude, latitude) != 0) return false;
        if (Double.compare(alarm.longtitude, longtitude) != 0) return false;
        if (switchOn != alarm.switchOn) return false;
        if (!calendarID.equals(alarm.calendarID)) return false;
        if (timeOfMeet != null ? !timeOfMeet.equals(alarm.timeOfMeet) : alarm.timeOfMeet != null)
            return false;
        if (timeOfAlarm != null ? !timeOfAlarm.equals(alarm.timeOfAlarm) : alarm.timeOfAlarm != null)
            return false;
        if (placeOfMeet != null ? !placeOfMeet.equals(alarm.placeOfMeet) : alarm.placeOfMeet != null)
            return false;
        if (nameOfEventInCalendar != null ? !nameOfEventInCalendar.equals(alarm.nameOfEventInCalendar) : alarm.nameOfEventInCalendar != null)
            return false;
        if (!nameOfEventCustom.equals(alarm.nameOfEventCustom)) return false;
        return extraTime != null ? extraTime.equals(alarm.extraTime) : alarm.extraTime == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (alarmID ^ (alarmID >>> 32));
        result = 31 * result + calendarID.hashCode();
        result = 31 * result + (timeOfMeet != null ? timeOfMeet.hashCode() : 0);
        result = 31 * result + (timeOfAlarm != null ? timeOfAlarm.hashCode() : 0);
        result = 31 * result + (placeOfMeet != null ? placeOfMeet.hashCode() : 0);
        result = 31 * result + (nameOfEventInCalendar != null ? nameOfEventInCalendar.hashCode() : 0);
        result = 31 * result + nameOfEventCustom.hashCode();
        result = 31 * result + (extraTime != null ? extraTime.hashCode() : 0);
        result = 31 * result + typeOfRelocating;
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longtitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (switchOn ? 1 : 0);
        return result;
    }
}
