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

public class SkuCRUD {

    public Activity activity;
    Cursor numRows;
    public static  Cursor nuRows_;
    Cursor cursor;
    int rows = 0;
    public static int rows_ = 0;


    public SkuCRUD(AppCompatActivity activity) {
        this.activity = activity;
    }

    public SkuCRUD() {
    }

    public void add( String id , String name , String productID ) {

        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.SKU_ID, id);
        values.put(SqlDatabaseHelper.SKU_NAME, name);
        values.put(SqlDatabaseHelper.SKU_PRODUCT_ID, productID);

        try {

            database.insert(SqlDatabaseHelper.TBL_SKUS, null, values);

        } catch (SQLException e) {

        }

    }




    public String [] getSKUs( String productID ) {
        String [] sku = new String[this.skuCount(productID)];
        int i = 0;
        if(this.skuCount(productID) > 0 ){

            try {
                cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.SKU_NAME+" FROM " + SqlDatabaseHelper.TBL_SKUS + " WHERE "+SqlDatabaseHelper.SKU_PRODUCT_ID+" = '"+productID.trim()+"'",
                        null);

                if (cursor.moveToFirst()) {
                    do {

                        if(cursor.getString(0).trim().length() > 0 ){
                            sku[i]  = cursor.getString(0).toString().trim();
                            i++;
                        }


                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {

                return null;
            }
            return sku;
        }else{
            return null;
        }

    }

    public String getSKUID( String skuName ) {

        try {
            cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.SKU_ID+" FROM " + SqlDatabaseHelper.TBL_SKUS + " WHERE "+SqlDatabaseHelper.SKU_NAME+" = '"+skuName.trim()+"'",
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

            database.delete(SqlDatabaseHelper.TBL_SKUS , null , null);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }
    }


    public int skuCount(  ) {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_SKUS, null);
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

    public int skuCount( String productID ) {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_SKUS + " WHERE " + SqlDatabaseHelper.SKU_PRODUCT_ID + " ='"+productID.trim()+"'", null);
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
