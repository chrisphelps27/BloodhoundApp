package com.mvctc.gw.bloodhoundapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by mcintosh47303 on 11/13/2017.
 */

public class connection extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... Data) {
        String responseString = "";
        try {
            String app = "Bloodhound", page = Data[0], content = Data[1], directory = "http://gw.mvctc.com/Class2018/smcintosh/UNHACKABLE/";
            Log.e(app, page + content);
            // HttpURLConnection urlConnection = (HttpURLConnection)
            // url.openConnection();
            String urlParameters = content;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            String request = directory + page;
            URL url2 = null;
            url2 = new URL(request);
            HttpURLConnection conn = (HttpURLConnection) url2.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try {
                DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            } catch (Exception e) {
                //Log.d(app, e.toString());
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseString += inputLine;
            }


            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        //Log.d("bloodhound", s);
        //LocActivity.response = s;
    }
}

