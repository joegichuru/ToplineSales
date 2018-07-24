package com.symatechlabs.toplinemarketing.competitoractivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.symatechlabs.toplinemarketing.MainActivity;
import com.symatechlabs.toplinemarketing.R;
import com.symatechlabs.toplinemarketing.asynctasks.addCompetitorActivities;
import com.symatechlabs.toplinemarketing.database.CompanyCRUD;
import com.symatechlabs.toplinemarketing.database.OutletCRUD;
import com.symatechlabs.toplinemarketing.database.ProductCRUD;
import com.symatechlabs.toplinemarketing.database.SkuCRUD;
import com.symatechlabs.toplinemarketing.database.SubOutletCRUD;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.FormFunctions;
import com.symatechlabs.toplinemarketing.utilities.NetworkTools;
import com.symatechlabs.toplinemarketing.utilities.Utilities;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by root on 2/19/18.
 */

public class CompetitorActivities extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Callback , DatePickerDialog.OnDateSetListener {

    public static Spinner skuSpinner, productSpinner, companySpinner , outletSpinner , subOutletSpinner;
    public static EditText priceOffer , startDate , endDate , mechanics;
    CompanyCRUD companyCRUD;
    OutletCRUD outletCRUD;
    SubOutletCRUD subOutletCRUD;
    ProductCRUD productCRUD;
    SkuCRUD skuCRUD;
    ArrayAdapter skuAdapter , productAdapter , companyAdapter , outletAdapter , subOutletAdapter;
    String NO_PRODUCTS = "No Products Available" , NO_SUB_OUTLETS = "No Sub Outlets" , NO_SKUS = "No SKUs Available";
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    NetworkTools networkTools;
    private GoogleMap mMap;
    private boolean mRequestingLocationUpdates = false;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    RelativeLayout addCompetitor;
    Utilities utilities;
    public static double lat = 0, lng = 0;
    public static String skuID , productID , subOutletID , companyID  , latitude , longitude;
    FormFunctions formFunctions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.competitor_activities);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Competitor Activities");

        companySpinner = (Spinner) findViewById(R.id.company);
        productSpinner = (Spinner) findViewById(R.id.product);
        skuSpinner = (Spinner) findViewById(R.id.sku);
        outletSpinner = (Spinner) findViewById(R.id.outlet);
        subOutletSpinner = (Spinner) findViewById(R.id.suboutlet);
        priceOffer = (EditText) findViewById(R.id.priceOffer);
        startDate = (EditText) findViewById(R.id.startDate);
        endDate = (EditText) findViewById(R.id.endDate);
        mechanics = (EditText) findViewById(R.id.mechanics);

        addCompetitor = (RelativeLayout) findViewById(R.id.addCompetitor);

        companyCRUD = new CompanyCRUD();
        productCRUD = new ProductCRUD();
        skuCRUD = new SkuCRUD();
        outletCRUD = new OutletCRUD();
        subOutletCRUD = new SubOutletCRUD();
        formFunctions = new FormFunctions();
        networkTools = new NetworkTools(CompetitorActivities.this);
        utilities = new Utilities(CompetitorActivities.this);

        companyAdapter = new ArrayAdapter(this , R.layout.spinner_text , companyCRUD.getCompanies());
        companyAdapter.setDropDownViewResource(R.layout.spinner_selector);

        outletAdapter = new ArrayAdapter(this , R.layout.spinner_text , outletCRUD.getOutlets());
        outletAdapter.setDropDownViewResource(R.layout.spinner_selector);

        companySpinner.setAdapter(companyAdapter);
        outletSpinner.setAdapter(outletAdapter);

        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_ALARM};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        buildGoogleApiClient();

        companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(productCRUD.getProducts( companyCRUD.getCompanyID(parent.getSelectedItem().toString().trim()) ) != null){

                    productAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , productCRUD.getProducts( companyCRUD.getCompanyID(parent.getSelectedItem().toString().trim()) ));
                    productAdapter.setDropDownViewResource(R.layout.spinner_selector);
                    productSpinner.setAdapter(productAdapter);
                }else{
                    productAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , new String[]{NO_PRODUCTS});
                    productAdapter.setDropDownViewResource(R.layout.spinner_selector);
                    productSpinner.setAdapter(productAdapter);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(parent.getSelectedItem().toString().trim().length() > 0 ){
                    if(!parent.getSelectedItem().toString().trim().equalsIgnoreCase(NO_PRODUCTS)){
                        skuAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , skuCRUD.getSKUs(productCRUD.getProductID(parent.getSelectedItem().toString().trim())));
                        skuAdapter.setDropDownViewResource(R.layout.spinner_selector);
                        skuSpinner.setAdapter(skuAdapter);
                    }else{
                        skuAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , new String[]{NO_SKUS});
                        skuAdapter.setDropDownViewResource(R.layout.spinner_selector);
                        skuSpinner.setAdapter(skuAdapter);
                    }

                }else{
                    skuAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , new String[]{NO_SKUS});
                    skuAdapter.setDropDownViewResource(R.layout.spinner_selector);
                    skuSpinner.setAdapter(skuAdapter);
                }





            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        outletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



                    if( subOutletCRUD.getSubOutlets( outletCRUD.getOutletID(parent.getSelectedItem().toString().trim())) != null){

                        subOutletAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , subOutletCRUD.getSubOutlets( outletCRUD.getOutletID(parent.getSelectedItem().toString().trim())) );
                        subOutletAdapter.setDropDownViewResource(R.layout.spinner_selector);
                        subOutletSpinner.setAdapter(subOutletAdapter);

                    }else{
                        subOutletAdapter = new ArrayAdapter(CompetitorActivities.this , R.layout.spinner_text , new String[]{NO_SUB_OUTLETS});
                        subOutletAdapter.setDropDownViewResource(R.layout.spinner_selector);
                        subOutletSpinner.setAdapter(subOutletAdapter);
                    }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        CompetitorActivities.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        addCompetitor.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {

                if (networkTools.checkConnectivity()) {

                    utilities.locationEnabled();
                    mLastLocation = LocationServices.FusedLocationApi
                            .getLastLocation(mGoogleApiClient);
                    startLocationUpdates();
                    if (mLastLocation != null) {

                        lat = mLastLocation.getLatitude();
                        lng = mLastLocation.getLongitude();


                        if (lat == 0 && lng == 0) {

                            Toast.makeText(CompetitorActivities.this, "Retrieving location...", Toast.LENGTH_SHORT).show();

                        }else{

                            if(formFunctions.hasValue(skuSpinner , productSpinner , companySpinner , outletSpinner , subOutletSpinner) && formFunctions.hasValue(priceOffer , mechanics) && !productSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(NO_PRODUCTS) && !skuSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(NO_SKUS) && !subOutletSpinner.getSelectedItem().toString().trim().equalsIgnoreCase(NO_SUB_OUTLETS)) {

                                skuID = skuCRUD.getSKUID(skuSpinner.getSelectedItem().toString().trim());
                                productID = productCRUD.getProductID(productSpinner.getSelectedItem().toString().trim());
                                subOutletID = subOutletCRUD.getSubOutletID(subOutletSpinner.getSelectedItem().toString().trim());
                                companyID = companyCRUD.getCompanyID(companySpinner.getSelectedItem().toString().trim());
                                latitude = Double.toString(lat);
                                longitude = Double.toString(lng);

                                new addCompetitorActivities(CompetitorActivities.this).execute();

                            }else{
                                Toast.makeText(CompetitorActivities.this , ConstantValues.FILL_IN_ALL_FIELDS_ERROR , Toast.LENGTH_SHORT).show();
                            }



                        }
                    }
                }else{
                    Toast.makeText(CompetitorActivities.this , ConstantValues.ERROR_INTERNET , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
        createLocationRequest();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.e("Period ", "Periodic location updates started!");

        } else {
            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.e("Periodic", "Periodic location updates stopped!");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap.setMyLocationEnabled(true);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }



    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        startDate.setText(Integer.toString(dayOfMonth)+"-"+Integer.toString(monthOfYear) +"-"+Integer.toString(year));
    }
}

