package com.symatechlabs.toplinemarketing.asynctasks;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.symatechlabs.toplinemarketing.MainActivity;
import com.symatechlabs.toplinemarketing.database.UserCRUD;
import com.symatechlabs.toplinemarketing.sales.Sales;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.JSONParser;
import com.symatechlabs.toplinemarketing.utilities.Utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * Created by root on 5/30/17.
 */

public class addSaleAsync extends AsyncTask<String, Integer, String> {

    int connection_timeout = 100000, socket_timeout = 100000;
    HttpParams http_params;
    static InputStream is = null;
    static JSONObject jObj = null;
    public static String json = "", codes, resultCode ;
    JSONObject jsonObject = null;
    JSONArray jsonArray = null;
    JSONParser jParser = null;
    public String phoneNumber2;
    public static ProgressDialog pd;
    AppCompatActivity actionBarActivity;
    String result , message = null;
    int clockInID = 0;
    Utilities utilities;
    boolean status = false;
    ContentBody cBody;
    UserCRUD userCRUD;



    public addSaleAsync( AppCompatActivity activity) {

        actionBarActivity = activity;
        pd = new ProgressDialog(actionBarActivity);
        http_params = new BasicHttpParams();
        userCRUD = new UserCRUD();
        jParser = new JSONParser();
        utilities = new Utilities();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd.setMessage("Working...");
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

                HttpPost httpPOst = new HttpPost(ConstantValues.BASE_URL + "add_sales");
                httpPOst.addHeader("Cache-Control", "no-cache");
                HttpConnectionParams.setConnectionTimeout(http_params, connection_timeout);
                HttpConnectionParams.setSoTimeout(http_params, socket_timeout);
                DefaultHttpClient httpClient = new DefaultHttpClient(http_params);

                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);


               if(Sales.file != null ){

                   if(Sales.CONTENT_TYPE == 1){

                       Bitmap bmp = BitmapFactory.decodeFile(Sales.file.getPath());
                   ByteArrayOutputStream bos = new ByteArrayOutputStream();
                   bmp.compress(Bitmap.CompressFormat.JPEG.JPEG, 70, bos);
                   InputStream in = new ByteArrayInputStream(bos.toByteArray());
                       cBody = new InputStreamBody(in, "image/jpeg", "img.jpeg");
                       entity.addPart("sale_photo" , cBody);
                   }else  if (Sales.CONTENT_TYPE == 2){
                       FileBody fileBody = new FileBody(new File(utilities.getRealPathFromURI(actionBarActivity , Sales.videoUri)));
                       entity.addPart("sale_photo" , fileBody);
                   }


               }

                entity.addPart("product_id" , new StringBody(Sales.product , Charset.forName("UTF-8")));
                entity.addPart("sku_id" , new StringBody(Sales.sku , Charset.forName("UTF-8")));
                entity.addPart("qty" , new StringBody(Sales.productQty , Charset.forName("UTF-8")));
                entity.addPart("sale_type" , new StringBody(Sales.sale , Charset.forName("UTF-8")));
                entity.addPart("names" , new StringBody(Sales.name.getText().toString().trim() , Charset.forName("UTF-8")));
                entity.addPart("phone" , new StringBody(Sales.phone.getText().toString().trim() , Charset.forName("UTF-8")));
                entity.addPart("latitude" , new StringBody(Double.toString(Sales.lat) , Charset.forName("UTF-8")));
                entity.addPart("longitude" , new StringBody(Double.toString(Sales.lng) , Charset.forName("UTF-8")));
                entity.addPart("user_id" , new StringBody(userCRUD.getUser("id") , Charset.forName("UTF-8")));
                httpPOst.setEntity(entity);

                //httpPOst.setEntity(new UrlEncodedFormEntity(nameValuePairs));
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


        Log.d("JSON" , json);
        if(jsonObject != null){
            try {
                status = jsonObject.getBoolean("status");
                message = jsonObject.getString("message");

                Sales.lat = 0; Sales.lng = 0; Sales.file = null;

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(status){

                Toast.makeText(actionBarActivity , message , Toast.LENGTH_SHORT).show();
                actionBarActivity.startActivity( new Intent(actionBarActivity , MainActivity.class));

            }else{
                Toast.makeText(actionBarActivity , ConstantValues.ERROR_POSTING , Toast.LENGTH_SHORT).show();

            }
        }else{
            Toast.makeText(actionBarActivity , "An Error Occured , Retry" , Toast.LENGTH_SHORT).show();
        }


    }


}

