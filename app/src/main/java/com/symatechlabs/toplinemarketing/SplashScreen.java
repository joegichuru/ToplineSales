package com.symatechlabs.toplinemarketing;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.symatechlabs.toplinemarketing.database.UserCRUD;


public class SplashScreen extends Activity {

    UserCRUD userCRUD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        userCRUD = new UserCRUD();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent(SplashScreen.this , Login.class));

                 if(userCRUD.userExists()){
                     startActivity(new Intent(SplashScreen.this , MainActivity.class));
                 } else{
                     startActivity(new Intent(SplashScreen.this , Login.class));
                 }
                // TODO Auto-generated method stub

            }
        }, 3000);
    }

}
