package com.symatechlabs.toplinemarketing.sales;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.mindorks.paracamera.Camera;
import com.symatechlabs.toplinemarketing.BuildConfig;
import com.symatechlabs.toplinemarketing.MainActivity;
import com.symatechlabs.toplinemarketing.R;
import com.symatechlabs.toplinemarketing.asynctasks.getProducts;
import com.symatechlabs.toplinemarketing.database.ProductCRUD;
import com.symatechlabs.toplinemarketing.database.SkuCRUD;
import com.symatechlabs.toplinemarketing.database.UserCRUD;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.NetworkTools;
import com.symatechlabs.toplinemarketing.utilities.Utilities;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by root on 2/19/18.
 */

public class Sales extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, Callback {

    ArrayAdapter arrayAdapter;
    public static Spinner skuSpinner, productSpinner, saleType;
    private AlarmManager alarms;
    AlarmManager alarmManager;
    private PendingIntent tracking;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    NetworkTools networkTools;
    private GoogleMap mMap;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private boolean mRequestingLocationUpdates = false;
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    static final int REQUEST_VIDEO_CAPTURE = 2;
    public static int CONTENT_TYPE = 0;
    Camera camera;
    ImageView cameraImg, videoImg;
    public static File file;
    public static String sku, product, productQty, sale;
    RelativeLayout addSale;
    public static EditText qty, name, phone;
    Utilities utilities;
    public static double lat = 0, lng = 0;
    ProductCRUD productCRUD;
    SkuCRUD skuCRUD;
    public static Uri videoUri;
    public static ProgressDialog pd;
    public static String videoFilePrefix = "";
    public Response response = null;
    File mediaFile;
    UserCRUD userCRUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Submit Sales");

        productCRUD = new ProductCRUD();
        skuCRUD = new SkuCRUD();
        networkTools = new NetworkTools(Sales.this);
        if (productCRUD.productCount() == 0) {
            if (networkTools.checkConnectivity()) {
                new getProducts(Sales.this).execute();
            } else {
                Toast.makeText(Sales.this, ConstantValues.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
            }
        }


        buildGoogleApiClient();
        final String array[] = productCRUD.getProducts();
        final String saleTypes[] = {"Select Option", "Consumer", "Trade Sales"};
        ArrayAdapter arrayAdapter4 = new ArrayAdapter(this, R.layout.spinner_text, saleTypes);
        arrayAdapter4.setDropDownViewResource(R.layout.spinner_selector);
        skuSpinner = (Spinner) findViewById(R.id.skuSpinner);
        productSpinner = (Spinner) findViewById(R.id.productSpinner);
        saleType = (Spinner) findViewById(R.id.saleType);
        cameraImg = (ImageView) findViewById(R.id.cameraImg);
        addSale = (RelativeLayout) findViewById(R.id.addSale);
        qty = (EditText) findViewById(R.id.qty);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.phone);
        videoImg = (ImageView) findViewById(R.id.videoImg);
        pd = new ProgressDialog(Sales.this);
        userCRUD = new UserCRUD();

        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_text, array);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_selector);

        productSpinner.setAdapter(arrayAdapter);
        utilities = new Utilities(Sales.this);

        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_text, saleTypes);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_selector);
        saleType.setAdapter(arrayAdapter4);

        productSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                String productName = parent.getItemAtPosition(position).toString().trim();
                if (skuCRUD.getSKUs(productCRUD.getProductID(productName)) != null) {


                    String skuArray[] = skuCRUD.getSKUs(productCRUD.getProductID(productName));
                    ArrayAdapter arrayAdapter2 = new ArrayAdapter(Sales.this, R.layout.spinner_text, skuArray);
                    arrayAdapter2.setDropDownViewResource(R.layout.spinner_selector);
                    arrayAdapter2.getCount();

                    for (int q = 0; q < skuArray.length; q++) {
                        Log.d("SKUs", skuArray[q]);
                    }

                    try {
                        skuSpinner.setAdapter(arrayAdapter2);
                    } catch (Exception e) {

                    }


                } else {
                    String array2[] = {"No SKUs available"};
                    ArrayAdapter arrayAdapter3 = new ArrayAdapter(Sales.this, R.layout.spinner_text, array2);
                    arrayAdapter3.setDropDownViewResource(R.layout.spinner_selector);
                    skuSpinner.setAdapter(arrayAdapter3);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SET_ALARM};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }


        cameraImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buildCamera(1);

                try {
                    camera.takePicture();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        videoImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordVideo();
            }
        });

        addSale.setOnClickListener(new View.OnClickListener() {
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

                            Toast.makeText(Sales.this, "Retrieving location...", Toast.LENGTH_SHORT).show();

                        } else {

                            if (!skuSpinner.getSelectedItem().toString().trim().equalsIgnoreCase("No SKUs available") && qty.getText().toString().trim().length() > 0 && name.getText().toString().trim().length() > 0) {

                                sku = skuCRUD.getSKUID(skuSpinner.getSelectedItem().toString().trim());
                                product = productCRUD.getProductID(productSpinner.getSelectedItem().toString().trim());
                                productQty = qty.getText().toString().trim();
                                if (saleType.getSelectedItem().toString().trim().equalsIgnoreCase("Consumer")) {
                                    sale = "1";
                                } else {
                                    sale = "2";
                                }


                                // if(file != null){
                                if (!saleType.getSelectedItem().toString().equalsIgnoreCase("Select Option")) {
                                    //new addSaleAsync(Sales.this).execute();
                                    //todo i have replace with this
                                    sendDataToserver();
                                } else {
                                    Toast.makeText(Sales.this, "Select Sale Type", Toast.LENGTH_SHORT).show();
                                }

                                // }else{
                                // Toast.makeText(Sales.this , "Kindly Take a Photo" , Toast.LENGTH_SHORT).show();
                                // }

                            } else {
                                Toast.makeText(Sales.this, ConstantValues.FILL_IN_ALL_FIELDS_ERROR, Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(Sales.this, "Retrieving location...", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(Sales.this, ConstantValues.ERROR_INTERNET, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    public void recordVideo() {
        //the filename prefix i.e timestamp_video
        videoFilePrefix = String.valueOf(System.currentTimeMillis()) + "_video";
        File f = new File(Environment.getExternalStorageDirectory(), "topline");
        if (!f.exists()) {
            f.mkdirs();
        }
        mediaFile =
                new File(
                        f.getPath()+ videoFilePrefix + ".mp4");


        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //securely create the video file and grant url permissions to support api 24>=
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        videoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", mediaFile);

        //this will crash the app on api 24>=
        /// Uri videoUri = Uri.fromFile(mediaFile);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
//        startActivityForResult(intent, VIDEO_CAPTURE);
//        Intent takeVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == 1 && camera != null) {

                CONTENT_TYPE = 1;

                file = new File(camera.getCameraBitmapPath());

                if (file.isFile()) {
                    mediaFile=file;
                    Toast.makeText(this.getApplicationContext(), "Photo Taken", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this.getApplicationContext(), "Photo not Taken", Toast.LENGTH_SHORT).show();
                }

            } else if (requestCode == REQUEST_VIDEO_CAPTURE) {
                CONTENT_TYPE = 2;
               // videoUri = data.getData();

               videoUri=FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".provider",mediaFile);

                //file = new File(videoUri.getPath());




            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void buildCamera(int section) {
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(section)
                .setDirectory("pics")
                .setName("topline_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);
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



    //send the clean data to server
    public void sendDataToserver() {

        pd.setMessage("Working...");
        pd.setCancelable(false);
        pd.show();

        Request request = null;
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        //todo sanitise this with real values captured
        //none file parts of the form
        builder.addFormDataPart("sku_id", sku);
        builder.addFormDataPart("product_id", product);
        builder.addFormDataPart("user_id", userCRUD.getUser("id"));
        builder.addFormDataPart("qty", productQty);
        builder.addFormDataPart("sale_type", sale);
        builder.addFormDataPart("names", name.getText().toString().trim());
        builder.addFormDataPart("phone", phone.getText().toString().trim());
        builder.addFormDataPart("latitude", String.valueOf(lat));
        builder.addFormDataPart("longitude", String.valueOf(lng));

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        //todo change this if possible coz the connection will retry fro 2 mins before timing out
        clientBuilder.connectTimeout(500, TimeUnit.SECONDS);
        clientBuilder.readTimeout(500,TimeUnit.SECONDS);
        //file parts of the form
        //add video
        if (mediaFile != null) {
            MediaType mediaType =null;
            if(CONTENT_TYPE==1){
                //file is photo
                Uri uri=Uri.fromFile(mediaFile);
                mediaType= MediaType.parse("image/jpeg");
            }else {
                //file is video
                mediaType= MediaType.parse(getMimeType(videoUri));
            }
            String filename =mediaFile.getName();
//            try {
//                InputStream inputStream = getContentResolver().openInputStream(videoUri);
//                int available=inputStream.available();
//                byte[] buffer=new byte[available];
//                inputStream.read(buffer);
//              //  File file=new File(buffer);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
           // filename = filename.substring(filename.lastIndexOf("/") + 1, filename.indexOf("."));
            Log.i("FILE NAME", filename);
            builder.addFormDataPart("sale_photo", filename, RequestBody.create(mediaType, mediaFile));
        }


        RequestBody requestBody = builder.build();
        request = new Request.Builder().post(requestBody).url(ConstantValues.SALES_URL).build();

        //do the network call
        OkHttpClient okHttpClient = clientBuilder.build();
        //show loading dialog
        okHttpClient.newCall(request).enqueue(this);
    }

    /**
     * gets file mime type e.g video/mp4
     *
     * @param uri
     * @return
     */
    public String getMimeType(Uri uri) {
        if (uri == null) {
            return "text";
        }
        ContentResolver contentResolver = this.getContentResolver();
        return contentResolver.getType(uri);
    }

    /**
     * called when there is an error sending the data to server
     *
     * @param call
     * @param e
     */
    @Override
    public void onFailure(Call call, IOException e) {
        //hide loading dialog and show error
        e.printStackTrace();
        Sales.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Sales.this, "Could not send data.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * our server response will be registered here if the request was successfull.
     *
     * @param call
     * @param rsp
     * @throws IOException
     */
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response rsp) throws IOException {
        //hide loading dialog and show success message
        pd.dismiss();
        response = rsp;
        try {
            String rspBody = response.body().string();
            Log.w("Video upload", rspBody);
            final JSONObject jsonObject=new JSONObject(rspBody);
            if(jsonObject.has("status")&&jsonObject.getString("status").equalsIgnoreCase("success")){
                final String message=jsonObject.getString("message");
                Sales.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Sales.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            }else {
                final String message=jsonObject.getString("message");
                Sales.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Sales.this, message, Toast.LENGTH_SHORT).show();
                    }
                });

            }

            startActivity( new Intent(Sales.this , MainActivity.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

