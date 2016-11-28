package com.lamadesign.smartalarm.Database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.lamadesign.smartalarm.Models.Alarm;

/**
 * Created by Adam on 07.08.2016.
 */
//kdyz se spousti tak nastavit v nastaveni working directory do urovne main
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?>[] classes = new Class[] {
            Alarm.class
    };
    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlite_config.txt", classes);
    }

}
