package com.symatechlabs.toplinemarketing.utilities;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by root on 4/10/17.
 */


public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser() {

    }

    public JSONObject getJSONFromUrl(String url) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }

    public String cleanString (String source, String pattern, String replacement) {
        if (source == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        int index;
        int patIndex = 0;
        while ((index = source.indexOf(pattern, patIndex)) != -1) {
            sb.append(source.substring(patIndex, index));
            sb.append(replacement);
            patIndex = index + pattern.length();
        }
        sb.append(source.substring(patIndex));
        return sb.toString();
    }

    public String clean(String toBeCleaned){
        String clean = "";
        clean = this.cleanString(toBeCleaned, "&quot;", "");
        clean = this.cleanString(toBeCleaned, ";nbsp;", " ");
        clean = this.cleanString(toBeCleaned, "amp", "");
        clean = this.cleanString(toBeCleaned, "&amp;", "");
        clean = this.cleanString(toBeCleaned, ";rdquo;", "\"");
        clean = this.cleanString(toBeCleaned, ";ldquo;", "\"");
        clean = this.cleanString(toBeCleaned, ";rsquo;", "'");
        clean = this.cleanString(toBeCleaned, ";#39;", "'");
        clean = this.cleanString(toBeCleaned, "#39;", "'");
        return clean;
    }




}
