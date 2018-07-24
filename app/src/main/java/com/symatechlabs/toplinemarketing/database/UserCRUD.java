package com.symatechlabs.toplinemarketing.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.symatechlabs.toplinemarketing.utilities.ConstantValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.symatechlabs.toplinemarketing.ToplineMarketing.database;


/**
 * Created by root on 4/19/17.
 */

public class UserCRUD {

    public Activity activity;
    Cursor numRows;
    Cursor cursor;
    int rows = 0;




    public UserCRUD(AppCompatActivity activity) {
        this.activity = activity;
    }

    public UserCRUD() {
    }

    public void add( String id , String name ,String email ) {

        this.delete();

        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.USER_ID, id);
        values.put(SqlDatabaseHelper.USER_NAME, name);
        values.put(SqlDatabaseHelper.USER_EMAIL, email);


        try {

            database.insert(SqlDatabaseHelper.TBL_USERS, null, values);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }


    }


    public void addLocation(String lat ,String longitude , String timeStamp) {

        this.deleteLocation();

        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.LAT, lat);
        values.put(SqlDatabaseHelper.LONG, longitude);
        values.put(SqlDatabaseHelper.TIME_STAMP, timeStamp);

        try {

            database.insert(SqlDatabaseHelper.TBL_LOCATION, null, values);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }


    }



    public String getUser(String field) {

        try {

            switch (field) {

                case "id":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.USER_ID + " FROM " + SqlDatabaseHelper.TBL_USERS,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;
                case "name":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.USER_NAME + " FROM " + SqlDatabaseHelper.TBL_USERS,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;
                case "email":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.USER_EMAIL + " FROM " + SqlDatabaseHelper.TBL_USERS,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;


            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }

    public String getLastLocation(String field) {

        try {

            switch (field) {

                case "lat":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.LAT + " FROM " + SqlDatabaseHelper.TBL_LOCATION,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;
                case "long":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.LONG + " FROM " + SqlDatabaseHelper.TBL_LOCATION,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;

                case "timestamp":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.TIME_STAMP + " FROM " + SqlDatabaseHelper.TBL_LOCATION,
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;

            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return "";
    }

    public boolean userExists() {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_USERS, null);
            numRows.moveToFirst();
            rows = numRows.getCount();
            if (rows == 0) {
                numRows.close();
                return false;
            } else {
                numRows.close();
                return true;
            }
        } catch (SQLException e) {
            Log.i("SQL_ERROR", e.getMessage());
        }
        return false;

    }

    public void delete(){

        try {

            database.delete(SqlDatabaseHelper.TBL_USERS , null , null);
            database.delete(SqlDatabaseHelper.TBL_SKUS , null , null);
            database.delete(SqlDatabaseHelper.TBL_PRODUCTS , null , null);
            database.delete(SqlDatabaseHelper.TBL_LOCATION , null , null);
            database.delete(SqlDatabaseHelper.TBL_COMPANIES , null , null);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void deleteLocation(){

        try {

            database.delete(SqlDatabaseHelper.TBL_LOCATION , null , null);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }
    }


    public String getLocations() {

        JSONArray jsonArray = new JSONArray();
        int i = 0;
        String userID = this.getUser("id");
        cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.LAT + " , "+SqlDatabaseHelper.LONG+ " , "+SqlDatabaseHelper.TIME_STAMP+" FROM " + SqlDatabaseHelper.TBL_LOCATION,
                null);


        if (cursor.moveToFirst()) {

            do {


                try {
                    JSONObject locations = new JSONObject();
                    locations.put("lat",cursor.getString(0).toString().trim());
                    locations.put("long", cursor.getString(1).toString().trim());
                    locations.put("timestamp", cursor.getString(2).toString().trim());
                    locations.put("userID", userID);
                    jsonArray.put(i , locations);
                    i++;

                } catch (JSONException e) {

                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        JSONObject finalObject = new JSONObject();

        try {
            finalObject.put("Location", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return finalObject.toString();

    }

}
