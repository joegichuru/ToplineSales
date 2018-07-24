package com.symatechlabs.toplinemarketing.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 4/7/17.
 */

public class DbFunctions {

    public SQLiteOpenHelper dbHelper;
    public SQLiteDatabase database;

    public static DbFunctions sInstance;

    int rows = 0;
    Cursor numRows;

    public DbFunctions( Context context ) {

        if(dbHelper == null){
            dbHelper = SqlDatabaseHelper.getInstance(context);
        }



    }

    public static synchronized DbFunctions getInstance(Context context){

        if (sInstance == null) {
            sInstance = new DbFunctions(context.getApplicationContext());
        }
        return sInstance;
    }

    public SQLiteDatabase open() {


        database = dbHelper.getWritableDatabase();

        return  database;

    }

    public void close() {
        database.close();
    }



    public int count(String query) {

        numRows = null;
        try {
            numRows = database.rawQuery(query, null);
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


}
