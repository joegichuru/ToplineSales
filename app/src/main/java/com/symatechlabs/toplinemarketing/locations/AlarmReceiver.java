package com.symatechlabs.toplinemarketing.locations;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by root on 7/11/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String DEBUG_TAG = "AlarmReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ALARM", "Recurring alarm; requesting location tracking.");
        // start the service
        try{
            Intent tracking = new Intent(context, LocationBackgroundService.class);
            context.startService(tracking);
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //context.startForegroundService(tracking);
            //}
        }catch (Exception e){

        }

    }
}

