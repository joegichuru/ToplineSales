package com.symatechlabs.toplinemarketing.database;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.symatechlabs.toplinemarketing.utilities.ConstantValues;

import static com.symatechlabs.toplinemarketing.ToplineMarketing.database;


/**
 * Created by root on 4/19/17.
 */

public class ImgCRUD {

    public Activity activity;
    Cursor numRows;
    public static  Cursor nuRows_;
    Cursor cursor;
    int rows = 0;
    public static int rows_ = 0;
    public static String BOARD_IMG = "1" , BUILDING_IMG = "2" , INSIDE_IMG = "3" , IMG_CAPTURED = "1" , IMG_UPLOADED = "2";




    public ImgCRUD(AppCompatActivity activity) {
        this.activity = activity;
    }

    public ImgCRUD() {
    }

    public void add( String type , String location , String sesssion , String status) {



        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.IMG_TYPE, type);
        values.put(SqlDatabaseHelper.IMG_LOCATION, location);
        values.put(SqlDatabaseHelper.IMG_SESSION, sesssion);
        values.put(SqlDatabaseHelper.IMG_STATUS, status);

        try {

            database.insert(SqlDatabaseHelper.TBL_IMGS, null, values);
            Toast.makeText(activity.getApplicationContext() , ConstantValues.PHOTO_SAVED , Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }


    }

    public void updateCustomerID( String customerID ) {

        try {

           // database.execSQL("UPDATE " + SqlDatabaseHelper.TBL_IMGS + " SET " + SqlDatabaseHelper.IMG_CUSTOMERID + " = '" + customerID.trim() + "' WHERE "+SqlDatabaseHelper.IMG_SESSION +" ='"+ AddCustomer.imgSession.trim()+"'");

        } catch (SQLException e) {
            Log.d("SQL_ERROR", e.getMessage());
        }


    }



    public String getImg(String field , String imgSession , String status ) {

        try {

            switch (field) {

                case "id":
                    cursor = database.rawQuery("SELECT app_location_id FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION + " ='"+imgSession+"' AND "+SqlDatabaseHelper.IMG_STATUS+" ='"+status.trim()+"' LIMIT 1",
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;

                case "customerID":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.IMG_CUSTOMERID + " FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION + " ='"+imgSession+"' AND "+SqlDatabaseHelper.IMG_STATUS+" ='"+status.trim()+"' LIMIT 1",
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;
                case "type":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.IMG_TYPE + " FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION + " ='"+imgSession+"' AND "+SqlDatabaseHelper.IMG_STATUS+" ='"+status.trim()+"'  LIMIT 1",
                            null);
                    if (cursor.moveToFirst()) {
                        do {
                            return cursor.getString(0).toString().trim();

                        } while (cursor.moveToNext());
                    }
                    break;
                case "location":
                    cursor = database.rawQuery("SELECT " + SqlDatabaseHelper.IMG_LOCATION + " FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION + " ='"+imgSession+"' AND "+SqlDatabaseHelper.IMG_STATUS+" ='"+status.trim()+"'  LIMIT 1",
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


    public void updateImageStatus(String imgID , String status ){
        try {

            database.execSQL("UPDATE " + SqlDatabaseHelper.TBL_IMGS + " SET " + SqlDatabaseHelper.IMG_STATUS + " = '" + status.trim() + "' WHERE app_location_id ='"+ imgID.trim()+"'");

        } catch (SQLException e) {
            Log.d("SQL_ERROR", e.getMessage());
        }
    }



    public void delete(){

        try {

            database.delete(SqlDatabaseHelper.TBL_IMGS , null , null);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }
    }


    public int imgCount( String sessionID , String status ) {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION+ " ='"+sessionID.trim()+"'  AND "+SqlDatabaseHelper.IMG_STATUS+" ='"+ status.trim()+"'", null);
            numRows.moveToFirst();
            rows = numRows.getCount();
            if (rows == 0) {
                numRows.close();
                return 0;
            } else {
                numRows.close();
                return rows;
            }
        } catch (SQLException e) {
            Log.i("SQL_ERROR", e.getMessage());
        }
        return 0;

    }

    public int totalImg(String sessionID){
        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION+ " ='"+sessionID.trim()+"'", null);
            numRows.moveToFirst();
            rows = numRows.getCount();
            if (rows == 0) {
                numRows.close();
                return 0;
            } else {
                numRows.close();
                return rows;
            }
        } catch (SQLException e) {
            Log.i("SQL_ERROR", e.getMessage());
        }
        return  0;
    }


    public static int totalImgs(String sessionID){
        nuRows_ = null;
        try {
            nuRows_ = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION+ " ='"+sessionID.trim()+"'", null);
            nuRows_.moveToFirst();
            rows_ = nuRows_.getCount();
            if (rows_ == 0) {
                nuRows_.close();
                return 0;
            } else {
                nuRows_.close();
                return rows_;
            }
        } catch (SQLException e) {
            Log.i("SQL_ERROR", e.getMessage());
        }
        return  0;
    }


    public int count(String sessionID , String type){


        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION+ " ='"+sessionID.trim()+"' AND "+SqlDatabaseHelper.IMG_TYPE + " ='"+type.trim()+"'", null);
            numRows.moveToFirst();
            rows = numRows.getCount();
            if (rows == 0) {
                numRows.close();
                return 0;
            } else {
                numRows.close();
                return rows;
            }
        } catch (SQLException e) {
            Log.i("SQL_ERROR", e.getMessage());
        }
        return  0;
    }


    public static int countStatic(String sessionID , String type){


        nuRows_ = null;
        try {
            nuRows_ = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_IMGS + " WHERE "+SqlDatabaseHelper.IMG_SESSION+ " ='"+sessionID.trim()+"' AND "+SqlDatabaseHelper.IMG_TYPE + " ='"+type.trim()+"'", null);
            nuRows_.moveToFirst();
            rows_ = nuRows_.getCount();
            if (rows_ == 0) {
                nuRows_.close();
                return 0;
            } else {
                nuRows_.close();
                return rows_;
            }
        } catch (SQLException e) {
            Log.i("SQL_ERROR", e.getMessage());
        }
        return  0;
    }
}
