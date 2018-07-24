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

public class CompanyCRUD {

    public Activity activity;
    Cursor numRows;
    public static  Cursor nuRows_;
    Cursor cursor;
    int rows = 0;
    public static int rows_ = 0;


    public CompanyCRUD(AppCompatActivity activity) {
        this.activity = activity;
    }

    public CompanyCRUD() {
    }

    public void add( String id , String name  ) {

        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.COMPANY_ID, id);
        values.put(SqlDatabaseHelper.COMPANY_NAME, name);

        try {

            database.insert(SqlDatabaseHelper.TBL_COMPANIES, null, values);

        } catch (SQLException e) {

        }

    }




    public String [] getCompanies(  ) {
        String [] companies = new String[this.companyCount()];
        int i = 0;
        if(this.companyCount() > 0 ){

            try {
                cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.COMPANY_NAME+" FROM " + SqlDatabaseHelper.TBL_COMPANIES ,
                        null);

                if (cursor.moveToFirst()) {
                    do {

                        if(cursor.getString(0).trim().length() > 0 ){
                            companies[i]  = cursor.getString(0).toString().trim();
                            i++;
                        }


                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {

                return null;
            }

        }else{
            return null;
        }
        return companies;
    }

    public String getCompanyID( String companyName ) {

        try {
            cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.COMPANY_ID+" FROM " + SqlDatabaseHelper.TBL_COMPANIES + " WHERE "+SqlDatabaseHelper.COMPANY_NAME+" = '"+companyName.trim()+"'",
                    null);
            if (cursor.moveToFirst()) {
                do {
                    return cursor.getString(0).toString().trim();


                } while (cursor.moveToNext());
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;
    }


    public void delete(){

        try {

            database.delete(SqlDatabaseHelper.TBL_COMPANIES , null , null);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }
    }


    public int companyCount(  ) {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_COMPANIES, null);
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



}
