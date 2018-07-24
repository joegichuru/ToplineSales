package com.symatechlabs.toplinemarketing.asynctasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.symatechlabs.toplinemarketing.database.UserCRUD;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.JSONParser;
import com.symatechlabs.toplinemarketing.utilities.Utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 5/30/17.
 */

public class getCustomer extends AsyncTask<String, Integer, String> {

    int connection_timeout = 100000, socket_timeout = 100000;
    HttpParams http_params;
    static InputStream is = null;
    static JSONObject jObj = null;
    public static String json = "", KEY_ITEM = "customer", codes, resultCode, customerID;
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    JSONParser jParser = null;
    public String phoneNumber2;
    public static ProgressDialog pd;
    AppCompatActivity actionBarActivity;
    String result;
    UserCRUD userCRUD;
    Utilities utilities;
    JSONObject c = null;

    public getCustomer(String customerID, AppCompatActivity activity) {

        this.customerID = customerID;
        actionBarActivity = activity;
        pd = new ProgressDialog(actionBarActivity);
        http_params = new BasicHttpParams();
        jParser = new JSONParser();
        userCRUD = new UserCRUD(activity);
        utilities = new Utilities(activity);


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setMessage("Retrieving Records...");
        pd.setCancelable(false);
        pd.show();

    }

    @Override
    protected String doInBackground(String... params) {


        while (!isCancelled()) {

            if (isCancelled()) {
                publishProgress(3);
                break;
            }

            // Making HTTP request
            try {

                HttpPost httpPOst = new HttpPost(ConstantValues.BASE_URL + "getCustomer.php");
                httpPOst.addHeader("Cache-Control", "no-cache");
                HttpConnectionParams.setConnectionTimeout(http_params, connection_timeout);
                HttpConnectionParams.setSoTimeout(http_params, socket_timeout);
                DefaultHttpClient httpClient = new DefaultHttpClient(http_params);
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("customerID", customerID));
                nameValuePairs.add(new BasicNameValuePair("userID", userCRUD.getUser("id")));

                httpPOst.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPOst);


                int code = httpResponse.getStatusLine().getStatusCode();
                codes = String.valueOf(code);
                if (code != HttpStatus.SC_OK && code == HttpStatus.SC_GATEWAY_TIMEOUT) {
                    httpClient.getConnectionManager().shutdown();
                    Log.d("ERROR_NET", "ERROR0");
                    return "Error";
                }
                Log.d("CODEs", codes);


                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.d("ERROR_NET1", "ERROR5");
                    return "Error";
                }

                // try parse the string to a JSON object

                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    Log.d("ERROR_NET2", e.getMessage());
                    return "Error";
                }



                    jObj = jsonObject;



                Log.d("ResultCode25", resultCode);


            } catch (UnsupportedEncodingException e) {
                Log.d("ERROR_NET4", "ERROR1");
                return "Error";
            } catch (ClientProtocolException e) {
                Log.d("ERROR_NET5", "ERROR2");
                return "Error";
            } catch (IOException e) {
                Log.d("ERROR_NET6", e.getMessage());
                return "Error";
            } catch (Exception e) {
                Log.d("ERROR_NET7", e.getMessage());
                return "Error";
            }


            if (isCancelled()) {

                publishProgress(3);
                break;
            }

            return codes;
        }
        return null;

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        pd.dismiss();

        if (jObj != null) {   //JSON ERRORS HANDLE!!!
            try {
                jsonArray = jObj.getJSONArray(KEY_ITEM);
            } catch (JSONException e) {
                e.printStackTrace();
            }



        } else {

        }


    }


}
