package com.symatechlabs.toplinemarketing.locations;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
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
 * Created by osorobrian on 7/17/17.
 */

public class syncToServer extends IntentService {

    int connection_timeout = 100000, socket_timeout = 100000;
    HttpParams http_params;
    public String codes, apiResult;
    static InputStream is = null;
    static JSONObject jObj = null;
    public static String json = "" , resultCode , KEY_ITEM = "addLocation";
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    JSONParser jParser = null;
    UserCRUD userCRUD;
    String  jsonString;
    public static int serviceCompleted = 1;
    public JSONArray jsonItemIDs;
    Utilities utilities;

    public syncToServer() {
        super("Location");
        http_params = new BasicHttpParams();
        jParser = new JSONParser();
        userCRUD = new UserCRUD();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        Bundle extras = intent.getExtras();

        if (extras == null) {

        } else {
            this.jsonString = (String) extras.get("json");


        }

        Log.d("LocationARRAY" , this.jsonString);


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        this.serviceCompleted = 0;
        int code = 0;
        utilities = new Utilities();
        this.apiResult = "ERROR";

        boolean runningSafe = true;
        try {
            while (runningSafe) {

                try {

                    HttpPost httpPOst = new HttpPost(ConstantValues.BASE_URL + "location");
                    HttpConnectionParams.setConnectionTimeout(http_params, connection_timeout);
                    HttpConnectionParams.setSoTimeout(http_params, socket_timeout);
                    DefaultHttpClient httpClient = new DefaultHttpClient(http_params);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("json", this.jsonString));
                    httpPOst.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse httpResponse = httpClient.execute(httpPOst);

                    code = httpResponse.getStatusLine().getStatusCode();
                    codes = String.valueOf(code);
                    Log.d("HTTP_CODE", codes);
                    if (code != HttpStatus.SC_OK
                            && code == HttpStatus.SC_GATEWAY_TIMEOUT) {
                        httpClient.getConnectionManager().shutdown();
                        Log.d("ERROR_NET", "ERROR0");
                        Log.d("HTTP_CODE", Integer.toString(code));
                        this.apiResult = "ERROR";
                        runningSafe = false;
                        break;

                    }

                    Log.d("HTTP_CODE2", Integer.toString(code));

                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();

                } catch (UnsupportedEncodingException e) {
                    Log.d("ERROR_NET2", e.getMessage());
                    this.apiResult = "ERROR";
                    runningSafe = false;
                    break;

                } catch (ClientProtocolException e) {
                    Log.d("ERROR_NET3", e.getMessage());
                    this.apiResult = "ERROR";
                    runningSafe = false;
                    break;

                } catch (IOException e) {
                    Log.d("ERROR_NET4", e.getMessage());
                    this.apiResult = "ERROR";
                    runningSafe = false;
                    break;

                } catch (Exception e) {
                    Log.d("ERROR_NET5", e.getMessage());
                    this.apiResult = "ERROR";
                    runningSafe = false;
                    break;

                }

                try {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.d("ERROR_NET6", e.getMessage());
                    this.apiResult = "ERROR";
                    runningSafe = false;
                    break;


                }

                try {
                    jsonObject = new JSONObject(json);
                } catch (JSONException e) {
                    Log.d("ERROR_NET7", e.getMessage());
                    this.apiResult = "ERROR";
                    runningSafe = false;
                    break;
                }

                try {

                    jObj = jsonObject;
                    if (jObj != null) {   //JSON ERRORS HANDLE!!!

                        jsonArray = jObj.getJSONArray(KEY_ITEM);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject c = jsonArray.getJSONObject(i);
                            resultCode = c.getString("result");



                        }

                        Log.d("JSON_API_RESULT" , json);

                        if(resultCode.equalsIgnoreCase("SUCCESS")){
                            Log.d("JSON_API_LOCATION_2" , resultCode);
                            userCRUD.deleteLocation();
                            break;
                        }

                    } else {
                        this.apiResult = "ERROR";
                        runningSafe = false;
                        break;
                    }


                } catch (JSONException e) {
                    this.apiResult = "ERROR";
                    Log.d("ERROR_NET8", e.getMessage());
                    runningSafe = false;
                    break;

                }


            }
        }catch (Exception e){

        }

        Log.d("LOCATION_SYNC", jsonString);
        Log.d("LOC_RESPONSE" , json);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.serviceCompleted = 1;
        Log.d("LOCATION_SYNC", jsonString);

    }
}

