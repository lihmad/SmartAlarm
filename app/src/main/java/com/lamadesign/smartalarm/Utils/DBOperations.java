package com.lamadesign.smartalarm.Utils;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.Database.DatabaseHelper;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Created by Adam on 07.08.2016.
 */
public class DBOperations {

    private static DatabaseHelper databaseHelper;

    private static DatabaseHelper getHelper(Context context){
        if (databaseHelper == null)
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
        return  databaseHelper;
    }

    private static void destroyHelper(){
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public static List<Alarm> getEvents(Context context){
        List<Alarm> alarmList = null;
        Dao<Alarm, Integer> alarmDao;

        try {
            alarmDao = getHelper(context).getAlarmDao();

            Date date = new Date();

            QueryBuilder<Alarm, Integer> queryBuilder = alarmDao.queryBuilder();
            queryBuilder.where().ge("timeOfAlarm", date);
            queryBuilder.orderBy("timeOfAlarm", true);

            alarmList = queryBuilder.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        destroyHelper();

        return alarmList;
    }

    public static void newAlarm(Context context, Alarm alarm){

        Dao<Alarm, Integer> alarmDao;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            alarmDao.create(alarm);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

    }

    public static void updateAlarm(Context context, Alarm alarm){

        Dao<Alarm, Integer> alarmDao;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            alarmDao.update(alarm);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();
    }

    public static Alarm getAlarmToWakeUp(Context context){
        Dao<Alarm, Integer> alarmDao;
        Alarm alarm = null;
        try {
            Date date = new Date();
            alarmDao = getHelper(context).getAlarmDao();
            QueryBuilder<Alarm, Integer> queryBuilder = alarmDao.queryBuilder();
            queryBuilder.where().gt("timeOfAlarm", date);
            queryBuilder.where().eq("switchOn", true);
            queryBuilder.orderBy("timeOfAlarm", true);

            List<Alarm> alarmFromCalendarList = queryBuilder.query();

            for(Alarm a : alarmFromCalendarList){
                if (a.getTimeOfAlarm().getTime() > date.getTime()){
                    alarm = a;
                    break;
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();
        return alarm;
    }

    public static Alarm getAlarm(Context context, Long id){
        Dao<Alarm, Integer> alarmDao;
        Alarm alarmFromCalendar = null;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            alarmFromCalendar = alarmDao.queryForId(id.intValue());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

        return alarmFromCalendar;
    }

    public static void deleteAlarm (Context context, Alarm alarm){
        Dao<Alarm, Integer> alarmDao;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            alarmDao.delete(alarm);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

    }
}
