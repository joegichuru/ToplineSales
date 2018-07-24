package com.symatechlabs.toplinemarketing.locations;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.symatechlabs.toplinemarketing.database.UserCRUD;
import com.symatechlabs.toplinemarketing.utilities.Utilities;

import java.text.DateFormat;
import java.util.TimeZone;



/**
 * Created by root on 7/11/17.
 */

public class LocationBackgroundService extends Service implements
        LocationListener {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private final String DEBUG_TAG = "UpdateLocation::Service";
    private LocationManager mgr;
    private String best;
    UserCRUD userCRUD;
    Utilities utilities;


    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @SuppressLint("MissingPermission")
        @Override
        public void handleMessage(Message msg) {


            try {
                Location location = mgr.getLastKnownLocation(best);
                mServiceHandler.post(new MakeToast(trackLocation(location)));
                // Stop the service using the startId, so that we don't stop
                // the service in the middle of handling another job
                stopSelf(msg.arg1);
            } catch (Exception e) {
                Log.d("SERVICE_ERROR_ANOTHER" , e.getMessage());
            }



        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                android.os.Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d(DEBUG_TAG, ">>>onCreate()");
        userCRUD = new UserCRUD();
        utilities = new Utilities();
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        mgr = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        best = mgr.getBestProvider(criteria, true);
        try {
            mgr.requestLocationUpdates(best, 15000, 1, this);
        } catch (Exception e) {

        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//      Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);
        Log.d(DEBUG_TAG, ">>>onStartCommand()");
        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
//    Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        Log.d(DEBUG_TAG, ">>>onDestroy()");
    }

    //obtain current location, insert into database and make toast notification on screen
    private String trackLocation(Location location) {
        double longitude;
        double latitude;
        String time;
        String result = "Location currently unavailable.";
        Log.d("LOCATION_SYNC2", "SENDING");
        // Insert a new record into the Events data source.
        // You would do something similar for delete and update.
        try {
            if (location != null) {


                longitude = location.getLongitude();
                latitude = location.getLatitude();


                userCRUD.addLocation(Double.toString(latitude), Double.toString(longitude), utilities.getDate() + " " + utilities.getTime());
                Log.d("LOCATION_SENT", "SENT" + userCRUD.getLocations());
                Intent salesServiceIntent = new Intent(this, syncToServer.class);

                salesServiceIntent.putExtra("json", userCRUD.getLocations());
                this.startService(salesServiceIntent);
            }
        } catch (Exception e) {
            Log.d("LOCATION_SYNC2", e.getMessage());
        }

        return result;
    }

    private String parseTime(long t) {
        DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
        df.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        String gmtTime = df.format(t);
        return gmtTime;
    }

    private class MakeToast implements Runnable {
        String txt;

        public MakeToast(String text) {
            txt = text;
        }

        public void run() {
            //Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
//		mHandler.post(new MakeToast(trackLocation(location)));
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.w(DEBUG_TAG, ">>>provider disabled: " + provider);
    }


    @Override
    public void onProviderEnabled(String provider) {
        Log.w(DEBUG_TAG, ">>>provider enabled: " + provider);
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.w(DEBUG_TAG, ">>>provider status changed: " + provider);
    }
}

