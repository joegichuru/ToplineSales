package com.symatechlabs.toplinemarketing.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by root on 4/7/17.
 */

public class SqlDatabaseHelper extends SQLiteOpenHelper {

    public static String DB_NAME = "igmarket.db";
    public static SqlDatabaseHelper sInstance;
    public static int DB_VER = 1;


    public static String TBL_USERS = "tbl_users" , TBL_LOCATION = "tbl_location" , TBL_IMGS = "tbl_imgs" ,
            TBL_OUTLET = "tbl_outlet" ,  TBL_PRODUCTS = "tbl_products" , TBL_SKUS = "tbl_skus" ,
            TBL_COMPANIES = "tbl_companies" , TBL_SUB_OUTLET = "tbl_suboutlet";

    public static String USER_ID = "user_id",
            USER_PHONE = "user_phone" , USER_NAME = "user_name" , USER_EMAIL = "user_email";

    public static String IMG_TYPE = "type",
            IMG_CUSTOMERID = "customerid" , IMG_LOCATION = "imgpath" , IMG_SESSION = "imgSession" , IMG_STATUS = "imgStatus";

    public static String LAT = "latidude",
            LONG = "longitude" , TIME_STAMP = "timestamp";

    public static String PRODUCT_NAME = "product_name",
            PRODUCT_ID = "product_id" , PRODUCT_COMPANY = "product_company";

    public static String COMPANY_NAME = "company_name",
            COMPANY_ID = "company_id";

    public static String SKU_NAME = "sku_name",
            SKU_ID = "sku_id" , SKU_PRODUCT_ID = "product_id";

    public static String OUTLET_NAME = "outletname" , OUTLET_ID = "outletid";

    public static String  SUB_OUTLET_NAME = "sub_outletname" , SUB_OUTLET_ID = "sub_outletid";


    public static String CREATE_TBL_USERS = "CREATE TABLE " + TBL_USERS
            + " (app_user_id INTEGER PRIMARY KEY AUTOINCREMENT , " + USER_NAME + " TEXT ," + USER_PHONE + " TEXT , " + USER_EMAIL + " TEXT , " + USER_ID + " TEXT )";

    public static String CREATE_TBL_LOCATIONS = "CREATE TABLE " + TBL_LOCATION
            + " (app_location_id INTEGER PRIMARY KEY AUTOINCREMENT , " + LAT + " TEXT , " + LONG + " TEXT  , " + TIME_STAMP + " TEXT )";


    public static String CREATE_TBL_IMGS = "CREATE TABLE " + TBL_IMGS
            + " (app_location_id INTEGER PRIMARY KEY AUTOINCREMENT , " + IMG_TYPE + " TEXT , " + IMG_CUSTOMERID + " TEXT  , " + IMG_LOCATION + " TEXT , " + IMG_SESSION + " TEXT  , " + IMG_STATUS + " TEXT )";


    public static String CREATE_TBL_OUTLET = "CREATE TABLE " + TBL_OUTLET
            + " (app_plan_id INTEGER PRIMARY KEY AUTOINCREMENT , " + OUTLET_ID + " TEXT , " + OUTLET_NAME + " TEXT  )";

    public static String CREATE_TBL_PRODUCTS = "CREATE TABLE " + TBL_PRODUCTS
            + " (app_product_id INTEGER PRIMARY KEY AUTOINCREMENT , " + PRODUCT_ID + " TEXT , " + PRODUCT_NAME + " TEXT , "+PRODUCT_COMPANY+ " TEXT )";

    public static String CREATE_TBL_SKUS = "CREATE TABLE " + TBL_SKUS
            + " (app_sku_id INTEGER PRIMARY KEY AUTOINCREMENT , " + SKU_ID + " TEXT , " + SKU_NAME + " TEXT  , " + SKU_PRODUCT_ID + " TEXT )";


    public static String CREATE_TBL_COMPANIES = "CREATE TABLE " + TBL_COMPANIES
            + " (app_sku_id INTEGER PRIMARY KEY AUTOINCREMENT , " + COMPANY_ID + " TEXT , " + COMPANY_NAME + " TEXT )";

    public static String CREATE_TBL_SUBOUTLET = "CREATE TABLE " + TBL_SUB_OUTLET
            + " (app_sku_id INTEGER PRIMARY KEY AUTOINCREMENT , " + SUB_OUTLET_ID + " TEXT , " + SUB_OUTLET_NAME + " TEXT , " + OUTLET_ID + " TEXT  )";



    public SqlDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public static synchronized SqlDatabaseHelper getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new SqlDatabaseHelper(context.getApplicationContext());
        }
        return sInstance;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        try {


            sqLiteDatabase.execSQL(CREATE_TBL_USERS);
            sqLiteDatabase.execSQL(CREATE_TBL_LOCATIONS);
            sqLiteDatabase.execSQL(CREATE_TBL_IMGS);
            sqLiteDatabase.execSQL(CREATE_TBL_OUTLET);
            sqLiteDatabase.execSQL(CREATE_TBL_PRODUCTS);
            sqLiteDatabase.execSQL(CREATE_TBL_SKUS);
            sqLiteDatabase.execSQL(CREATE_TBL_COMPANIES);
            sqLiteDatabase.execSQL(CREATE_TBL_SUBOUTLET);

        } catch (SQLException e) {
            Log.d("SQL_ERROR_ONCREATE", e.getMessage().toString());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {


        if (newVersion > oldVersion) {


        }

    }
}
