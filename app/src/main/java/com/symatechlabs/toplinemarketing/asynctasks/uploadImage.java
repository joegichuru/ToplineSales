package com.symatechlabs.toplinemarketing.asynctasks;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.symatechlabs.toplinemarketing.database.ImgCRUD;
import com.symatechlabs.toplinemarketing.utilities.ConstantValues;
import com.symatechlabs.toplinemarketing.utilities.mediaFunctions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@SuppressLint("NewApi")
public class uploadImage extends AsyncTask<String, Integer, String> {

    Context context;
    String source , bufferedResult ,fileLocation , imgID , customerID , type , totalImgs , uploadedImg;
    StringBuilder stringBuilder;
    AppCompatActivity activity;
    int serverResponseCode , initial = 0;
    public ProgressDialog pd;
    Uri selectedImage;
    mediaFunctions mfunctions;
    ImgCRUD imgCRUD;



    public uploadImage(AppCompatActivity activity , String customerID) {
        context = activity.getApplicationContext();
        this.activity = activity;
        this.customerID = customerID;
        stringBuilder = new StringBuilder();
        imgCRUD = new ImgCRUD(activity);
        pd = new ProgressDialog(activity);

        //fileLocation = imgCRUD.getImg("location" , AddCustomer.imgSession , com.symatechlabs.trancewood.database.ImgCRUD.IMG_CAPTURED);
        //imgID = imgCRUD.getImg("id" , AddCustomer.imgSession , com.symatechlabs.trancewood.database.ImgCRUD.IMG_CAPTURED);
        //type = imgCRUD.getImg("type" , AddCustomer.imgSession , com.symatechlabs.trancewood.database.ImgCRUD.IMG_CAPTURED);
        //totalImgs = Integer.toString(imgCRUD.totalImg(AddCustomer.imgSession));
        //uploadedImg = Integer.toString(imgCRUD.imgCount(AddCustomer.imgSession , com.symatechlabs.trancewood.database.ImgCRUD.IMG_UPLOADED ) + 1 );
    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        pd.setMessage("Uploading..."+uploadedImg+" of "+totalImgs);
        pd.setCancelable(false);
        pd.show();

    }

    @Override
    protected String doInBackground(String... unused) {

            while (!isCancelled()) {

                if(isCancelled()){
                    publishProgress(3);
                    break;
                }

                String fileName = source;
                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1 * 1024 * 1024;
                File sourceFile = new File(fileLocation);

                if (!sourceFile.isFile()) {
                    Log.d("ERROR_IMG" , "NO_IMG");
                    break;
                }
                else
                {
                    try {

                        while(!isCancelled()){

                            if(isCancelled()){
                                publishProgress(3);
                                break;
                            }

                            FileInputStream fileInputStream = new FileInputStream(sourceFile);
                            URL url = new URL (ConstantValues.BASE_URL+"uploadImage.php?customerID="+customerID.trim()+"&type="+type.trim());

                            // Open a HTTP  connection to  the URL
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true); // Allow Inputs
                            conn.setDoOutput(true); // Allow Outputs
                            conn.setUseCaches(false); // Don't use a Cached Copy
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Connection", "Keep-Alive");
                            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                            conn.setRequestProperty("myfile", fileName);
                            conn.setRequestProperty("customerID", customerID);

                            dos = new DataOutputStream(conn.getOutputStream());

                            dos.writeBytes(twoHyphens + boundary + lineEnd);
                            dos.writeBytes("Content-Disposition: form-data; name=\"myfile\";filename=\"myfile.jpg"+ lineEnd);
                            dos.writeBytes(lineEnd);

                            // create a buffer of  maximum size
                            bytesAvailable = fileInputStream.available();

                            bufferSize = Math.min(bytesAvailable, maxBufferSize);
                            buffer = new byte[bufferSize];

                            // read file and write it into form...
                            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            while (bytesRead > 0) {

                                dos.write(buffer, 0, bufferSize);
                                bytesAvailable = fileInputStream.available();
                                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                            }

                            // send multipart form data necesssary after file data...
                            dos.writeBytes(lineEnd);
                            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                            // Responses from the server (code and message)
                            serverResponseCode = conn.getResponseCode();
                            String serverResponseMessage = conn.getResponseMessage();

                            InputStream stream = conn.getInputStream();
                            InputStreamReader isReader = new InputStreamReader(stream);
                            BufferedReader br = new BufferedReader(isReader);

                            while( (bufferedResult = br.readLine()) != null){

                                stringBuilder.append(bufferedResult);

                            }

                            fileInputStream.close();
                            dos.flush();
                            dos.close();


                            if (isCancelled()) {

                                publishProgress(3);
                                break;
                            }
                            return "ss";
                        }
                    } catch (Exception e) {
                        Log.i("HIR", "" + e.getMessage());
                        return "-1";
                    }
                }


        }
        return "";
    }

    @Override
    protected void onPostExecute(String result) {

        pd.dismiss();
       // imgCRUD.updateImageStatus( imgID , com.symatechlabs.trancewood.database.ImgCRUD.IMG_UPLOADED );

        if(!result.equalsIgnoreCase("-1")){

            if(stringBuilder.toString().trim().equalsIgnoreCase("ERROR") ){
                Toast.makeText(context, "An error occured uploading image , try again", Toast.LENGTH_SHORT).show();
            }else {

            }

            /*if(imgCRUD.imgCount(AddCustomer.imgSession , com.symatechlabs.trancewood.database.ImgCRUD.IMG_CAPTURED) > 0){
                new uploadImage(activity , customerID).execute();
            }else{
                activity.startActivity( new Intent(activity , MainActivity.class));
                Toast.makeText(context, ConstantValues.SUCCESS_POSTING , Toast.LENGTH_SHORT).show();
            }*/

        }else{
            Toast.makeText(context, "An error was encountered, check your connectivity and try again later.", Toast.LENGTH_SHORT).show();
        }

    }






}