package com.symatechlabs.toplinemarketing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.symatechlabs.toplinemarketing.asynctasks.getProducts;
import com.symatechlabs.toplinemarketing.database.ProductCRUD;
import com.symatechlabs.toplinemarketing.database.UserCRUD;
import com.symatechlabs.toplinemarketing.dialogs.attendanceDialog;
import com.symatechlabs.toplinemarketing.competitoractivities.CompetitorActivities;
import com.symatechlabs.toplinemarketing.orderdelivery.OrderDelivery;
import com.symatechlabs.toplinemarketing.sales.Sales;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.NetworkTools;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    NavigationView nav_view;
    UserCRUD userCRUD;
    NetworkTools networkTools;
    LinearLayout productavailability , competitoractivities , orderdelivery , attendance;
    WindowManager.LayoutParams lp;
    DisplayMetrics metrics;
    TextView credits;
    ProductCRUD productCRUD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        userCRUD = new UserCRUD();
        productCRUD = new ProductCRUD();
        networkTools = new NetworkTools(MainActivity.this);

        if(productCRUD.productCount() == 0){
            if(networkTools.checkConnectivity()){
                new getProducts(MainActivity.this).execute();
            }else {
                Toast.makeText(MainActivity.this , ConstantValues.ERROR_INTERNET , Toast.LENGTH_SHORT).show();
            }
        }

        nav_view = (NavigationView) findViewById(R.id.nav_view);
        View headerView = nav_view.getHeaderView(0);
        TextView sideEmail = (TextView) headerView.findViewById(R.id.sideEmail);
        TextView sideName = (TextView) headerView.findViewById(R.id.sideName);

        productavailability = (LinearLayout) findViewById(R.id.productavailability);
        competitoractivities = (LinearLayout) findViewById(R.id.competitorActivities);
        orderdelivery = (LinearLayout) findViewById(R.id.orderdelivery);
        attendance = (LinearLayout) findViewById(R.id.attendance);
        credits = (TextView) findViewById(R.id.credits);



        credits.setText(Html.fromHtml("<b>Topline Marketing</b> &copy; <b>2018</b>"));

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://symatechlabs.com/"));
                startActivity(i);
            }
        });


        sideName.setText(userCRUD.getUser("name"));
        sideEmail.setText(userCRUD.getUser("email"));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        productavailability.setOnClickListener(this);
        competitoractivities.setOnClickListener(this);
        orderdelivery.setOnClickListener(this);
        attendance.setOnClickListener(this);

        lp = new WindowManager.LayoutParams();
        metrics = MainActivity.this.getResources().getDisplayMetrics();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

         if (id == R.id.logout) {

            userCRUD.delete();
            startActivity(new Intent(MainActivity.this, Login.class));

        }else if(id == R.id.refresh){
             if(networkTools.checkConnectivity()){
                 new getProducts(MainActivity.this).execute();
             }else {
                 Toast.makeText(MainActivity.this , ConstantValues.ERROR_INTERNET , Toast.LENGTH_SHORT).show();
             }
         }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.productavailability:
                startActivity( new Intent(MainActivity.this , Sales.class));
                break;

            case R.id.competitorActivities:
                startActivity( new Intent(MainActivity.this , CompetitorActivities.class));
                break;

            case R.id.orderdelivery:
                startActivity( new Intent(MainActivity.this , OrderDelivery.class));
                break;

            case R.id.attendance:
                attendanceDialog aboutCustomDialog = new attendanceDialog(MainActivity.this);
                lp = new WindowManager.LayoutParams();
                metrics = MainActivity.this.getResources().getDisplayMetrics();
                lp.copyFrom(aboutCustomDialog.getWindow().getAttributes());
                lp.width = (int) (metrics.widthPixels * 0.80);
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                aboutCustomDialog.show();
                aboutCustomDialog.getWindow().setAttributes(lp);
                aboutCustomDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                break;

                /*                */
        }

    }
}
