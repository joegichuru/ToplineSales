package com.symatechlabs.toplinemarketing.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by root on 4/10/17.
 */

public class NetworkTools {

    public ConnectivityManager conMgr;
    public NetworkInfo netInfo;
    public Context context;

    public NetworkTools(Context context) {
        this.context = context;
        this.conMgr = (ConnectivityManager) this.context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean checkConnectivity() {

        this.netInfo = this.conMgr.getActiveNetworkInfo();

        if (this.netInfo != null && this.netInfo.isConnectedOrConnecting() && this.netInfo.isAvailable()) {
            return true;
        } else {

            return false;
        }

    }

    public boolean gsmNetworkAvailable() {

        return true;
    }

}
