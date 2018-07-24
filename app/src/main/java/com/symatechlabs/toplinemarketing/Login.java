package com.symatechlabs.toplinemarketing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.symatechlabs.toplinemarketing.asynctasks.validateUserAsync;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.FormFunctions;
import com.symatechlabs.toplinemarketing.utilities.NetworkTools;


public class Login extends AppCompatActivity {

    EditText email , password;
    Button loginButton;
    NetworkTools networkTools;
    FormFunctions formFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        loginButton = (Button) findViewById(R.id.loginButton);

        String[] PERMISSIONS = {Manifest.permission.INTERNET , Manifest.permission.WRITE_EXTERNAL_STORAGE , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.SET_ALARM };
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        networkTools = new NetworkTools(Login.this);
        formFunctions = new FormFunctions();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               if(formFunctions.hasValue(email , password)){
                   if(networkTools.checkConnectivity()){

                       new validateUserAsync(email.getText().toString().trim() , password.getText().toString().trim() , Login.this).execute();

                   }else{
                       Toast.makeText(Login.this , ConstantValues.ERROR_INTERNET , Toast.LENGTH_LONG).show();
                   }
               }else{
                   Toast.makeText(Login.this , ConstantValues.FILL_IN_ALL_FIELDS_ERROR , Toast.LENGTH_LONG).show();
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



}
