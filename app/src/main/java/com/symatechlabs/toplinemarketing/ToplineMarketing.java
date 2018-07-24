package com.symatechlabs.toplinemarketing;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.symatechlabs.toplinemarketing.database.DbFunctions;
import com.symatechlabs.toplinemarketing.locations.AlarmReceiver;


/**
 * Created by root on 7/6/17.
 */

public class ToplineMarketing extends Application {

    public static DbFunctions dbFunctions;
    public static SQLiteDatabase database;
    private PendingIntent tracking;
    private AlarmManager alarms;
    AlarmManager alarmManager;


    @Override
    public void onCreate() {
        super.onCreate();
        dbFunctions = DbFunctions.getInstance(getApplicationContext());
        database = dbFunctions.open();

        alarms = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        boolean alarmUp = (PendingIntent.getBroadcast(ToplineMarketing.this, 0,
                new Intent(ToplineMarketing.this, AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            // Toast.makeText(TranceWood.this, "Alarm  Set", Toast.LENGTH_LONG).show();
        } else {
            setRecurringAlarm();
        }

    }

    private void setRecurringAlarm() {

        try {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long interval = AlarmManager.INTERVAL_HALF_HOUR;
            long timeToRefresh = SystemClock.elapsedRealtime() + interval;

            Intent intent = new Intent(ToplineMarketing.this, AlarmReceiver.class);
            tracking = PendingIntent.getBroadcast(ToplineMarketing.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.setInexactRepeating(alarmType, 60 * 1000 * 30, 60 * 1000 * 30, tracking);

        } catch (Exception e) {

            Log.d("ERROR_LOC" , e.getMessage());

        }
    }
}
