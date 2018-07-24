package com.symatechlabs.toplinemarketing.dialogs;

/**
 * Created by root on 2/20/18.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.symatechlabs.toplinemarketing.R;
import com.symatechlabs.toplinemarketing.utilities.Utilities;


/**
 * Created by root on 8/1/16.
 */
public class attendanceDialog extends Dialog implements View.OnClickListener {


    public Context context;
    AppCompatActivity activity;
    Utilities utilities;





    public attendanceDialog(Context context) {

        super(context);
        this.context = context;
        activity = (AppCompatActivity) context;
        utilities = new Utilities();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.attendance_dialog);




    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {





        }

    }


}
