package com.symatechlabs.toplinemarketing.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Created by root on 4/10/17.
 */

public class Utilities {

    SimpleDateFormat simpleDateFormat;
    Calendar calendar;
    public static int toDate = 1, fromDate = 2;
    AppCompatActivity activity;

    public String deviceName;
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();
    Random rand;
    WifiManager manager;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public Utilities() {

    }

    public Utilities(AppCompatActivity activity) {

        this.activity = activity;

    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static String getDeviceIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : networkInterfaces) {
                List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress inetAddress : inetAddresses) {
                    if (!inetAddress.isLoopbackAddress()) {
                        String sAddr = inetAddress.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                // drop ip6 port suffix
                                int delim = sAddr.indexOf('%');
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public String getMacAddress(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }
    public String getMacAddress() {
        try {
            return Settings.Secure.getString(this.activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return null;
        }
    }

    public String getDeviceDetails(Context context){

        TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

        String deviceName = android.os.Build.MODEL;
        String deviceMan = android.os.Build.MANUFACTURER;
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);

        return hasValue(manager.getNetworkOperatorName()) +"*"+ android.os.Build.MODEL +"*"+ android.os.Build.MANUFACTURER +"*"+ Integer.toString(resultCode);


    }

    @SuppressLint("MissingPermission")
    public String getMSISDN(Context context){

        TelephonyManager tMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(tMgr.getLine1Number() != null){
            return tMgr.getLine1Number();
        }else{
            return  "N/A";
        }


    }

    public String hasValue(String string){
         return  string.trim().length() > 0 ? string : "NULL";
    }

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    public int randomInt(int min, int max) {
        rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public String getMacAddress2() {

        manager = (WifiManager) this.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return info.getMacAddress();
    }

    public String getIPAddress() {

        manager = (WifiManager) this.activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        return Formatter.formatIpAddress(manager.getConnectionInfo().getIpAddress());
    }

    public String deviceName() {
        return Build.MANUFACTURER + ":" + android.os.Build.MODEL;
    }

    public String getDate() {


        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    }

    public String getTime() {

        return new SimpleDateFormat("HH:mm:ss").format(new Date());
    }

    public String getOSVersion() {
        return  Build.VERSION.RELEASE +" : "+ Integer.toString(Build.VERSION.SDK_INT);
    }

    public boolean isGooglePlayServicesAvailable(AppCompatActivity activity) {

        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public String getDate(int whatDate) {

        calendar = Calendar.getInstance();

        switch (whatDate) {

            case 1:
                calendar.add(Calendar.DATE, -1);
                return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());


            case 2:

                calendar.add(Calendar.DATE, +0);
                return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

        }

        return null;

    }

    public boolean locationEnabled() {

        LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        boolean netEnabled = false;

        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            netEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        if (!gpsEnabled && !netEnabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setMessage("Enable Location Services");
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(myIntent);
                    // get gps
                }
            });
            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();

            return false;
        } else {

            return true;
        }

    }

    public boolean checkPlayServices(AppCompatActivity activity) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                //activity.finish();
            }
            return false;
        }
        return true;
    }


    public String generateToken(){

        try{
            Long tsLong = System.currentTimeMillis()/1000;
            return  tsLong.toString()+randomString(12);
        }catch(Exception e){
            return  getDate()+randomString(12);
        }

    }

}
