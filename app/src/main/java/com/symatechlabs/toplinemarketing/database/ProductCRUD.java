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

public class ProductCRUD {

    public Activity activity;
    Cursor numRows;
    public static  Cursor nuRows_;
    Cursor cursor;
    int rows = 0;
    public static int rows_ = 0;


    public ProductCRUD(AppCompatActivity activity) {
        this.activity = activity;
    }

    public ProductCRUD() {
    }

    public void add( String id , String name , String companyID ) {


        ContentValues values = new ContentValues();
        values.put(SqlDatabaseHelper.PRODUCT_ID, id);
        values.put(SqlDatabaseHelper.PRODUCT_NAME, name);
        values.put(SqlDatabaseHelper.PRODUCT_COMPANY, companyID);

        try {

            database.insert(SqlDatabaseHelper.TBL_PRODUCTS, null, values);

        } catch (SQLException e) {

        }

    }




    public String [] getProducts( String companyID ) {

        String [] products = new String[this.productCount(companyID)];
        if(this.productCount(companyID) > 0 ){
            int i = 0;
            try {
                cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.PRODUCT_NAME+" FROM " + SqlDatabaseHelper.TBL_PRODUCTS + " WHERE "+SqlDatabaseHelper.PRODUCT_COMPANY +" ='"+companyID+"'",
                        null);
                if (cursor.moveToFirst()) {
                    do {
                        Log.d("PRODUCT" , cursor.getString(0).toString().trim());
                        products[i]  = cursor.getString(0).toString().trim();
                        i++;

                    } while (cursor.moveToNext());
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

            return products;
        }else{
            return null;
        }


    }

    public String [] getProducts(  ) {
        String [] products = new String[this.productCount()];

        int i = 0;
        try {
            cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.PRODUCT_NAME+" FROM " + SqlDatabaseHelper.TBL_PRODUCTS + " WHERE "+SqlDatabaseHelper.PRODUCT_COMPANY,
                    null);
            if (cursor.moveToFirst()) {
                do {
                    products[i]  = cursor.getString(0).toString().trim();
                    i++;

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return products;
    }

    public String getProductID( String productName ) {

        try {
            cursor = database.rawQuery("SELECT "+SqlDatabaseHelper.PRODUCT_ID+" FROM " + SqlDatabaseHelper.TBL_PRODUCTS + " WHERE "+SqlDatabaseHelper.PRODUCT_NAME + " ='"+productName.trim()+"'",
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

            database.delete(SqlDatabaseHelper.TBL_PRODUCTS , null , null);

        } catch (SQLException e) {
            Toast.makeText(activity.getApplicationContext(), ConstantValues.SQL_ERROR, Toast.LENGTH_LONG).show();
        }
    }


    public int productCount(  ) {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_PRODUCTS, null);
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

    public int productCount( String companyID ) {

        numRows = null;
        try {
            numRows = database.rawQuery("SELECT * FROM " + SqlDatabaseHelper.TBL_PRODUCTS + " WHERE " + SqlDatabaseHelper.PRODUCT_COMPANY + " ='"+companyID.trim()+"'", null);
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
