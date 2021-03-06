package com.lamadesign.smartalarm.Database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
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

    public static void addNewAlarms(List<Alarm> alarms, Context context){
        Dao<Alarm, Integer> alarmDao = null;
        try{
            alarmDao = getHelper(context).getAlarmDao();
        }catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        for(Alarm alarm : alarms){
            try {
                alarmDao.create(alarm);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        destroyHelper();
    }

    public static void updateAlarms(List<Alarm> alarms, Context context){
        Dao<Alarm, Integer> alarmDao = null;
        try{
            alarmDao = getHelper(context).getAlarmDao();
        }catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        for(Alarm alarm : alarms){
            try {
                alarmDao.update(alarm);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        destroyHelper();
    }

    public static void setCalendarDB(Alarm alarm, Context context){

        try {
            final Dao<Alarm, Integer> alarmDao = getHelper(context).getAlarmDao();

            QueryBuilder<Alarm, Integer> queryBuilder = alarmDao.queryBuilder();
            Where where = queryBuilder.where();
            where.eq("calendarID", alarm.getCalendarID());
            Alarm alarmDB = queryBuilder.queryForFirst();
            if (alarmDB == null)
                alarmDao.createOrUpdate(alarm);
            else {
                alarmDao.update(alarm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();
    }

    public static Alarm getAlarm(Context context, Long id){
        Dao<Alarm, Integer> alarmDao;
        Alarm alarm = null;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            alarm = alarmDao.queryForId(id.intValue());

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

        return alarm;
    }

    public static List<Alarm> getAlarmsBeforeTimeForDelete(Context context, Date date, List<String> googleids) {
        Dao<Alarm, Integer> alarmDao;
        List<Alarm> alarms = null;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            QueryBuilder<Alarm, Integer> queryBuilder = alarmDao.queryBuilder();
            queryBuilder.where().lt("timeOfMeetInCalendar", date);
            queryBuilder.where().not().in("calendarID", googleids);

            alarms = queryBuilder.query();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

        return alarms;
    }

    public static void bulkDelete(Context context, List<Alarm> alarms){
        Dao<Alarm, Integer> alarmDao;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            alarmDao.delete(alarms);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

    }

    public static Alarm getAlarmByGoogleId(Context context, String id){
        Dao<Alarm, Integer> alarmDao;
        Alarm alarm = null;
        try {
            alarmDao = getHelper(context).getAlarmDao();
            List<Alarm> alarms = alarmDao.queryForEq("calendarID", id);
            if(!alarms.isEmpty())
                alarm = alarms.get(0);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        destroyHelper();

        return alarm;

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
