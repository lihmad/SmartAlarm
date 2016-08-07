package com.lamadesign.smartalarm.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.lamadesign.smartalarm.Models.Alarm;
import com.lamadesign.smartalarm.R;

import java.sql.SQLException;

/**
 * Created by Adam on 07.08.2016.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "alarms.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Alarm, Integer> alarmDao;

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try{
            TableUtils.createTableIfNotExists(connectionSource, Alarm.class);
        }catch (SQLException e){
            Log.e(DatabaseHelper.class.getName(), "Nelze vytvorit db", e);

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try{
            TableUtils.dropTable(connectionSource, Alarm.class, true);
            onCreate(database, connectionSource);

        }catch (SQLException e){
            Log.e(DatabaseHelper.class.getName(), "Nelze povysit db z verze: " + oldVersion + " na verzi: " + newVersion, e);

        }
    }

    @Override
    public void close() {
        super.close();
        alarmDao = null;
    }

    public Dao<Alarm, Integer> getAlarmDao() throws SQLException{
        if (alarmDao == null){
            alarmDao = getDao(Alarm.class);
        }
        return alarmDao;
    }
}
