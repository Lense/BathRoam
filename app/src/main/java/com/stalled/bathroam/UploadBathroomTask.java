package com.stalled.bathroam;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class UploadBathroomTask extends AsyncTask<String,Void,Void> {
    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection httpconnect;
        URL url;

        try {

            url = new URL(params[0]);

            try {

                httpconnect = (HttpURLConnection) url.openConnection();
                httpconnect.setDoOutput(true);
                httpconnect.setDoInput(true);
                httpconnect.setInstanceFollowRedirects(false);
                httpconnect.setChunkedStreamingMode(0);

                try {

                    httpconnect.setRequestMethod("POST");
                    httpconnect.setUseCaches(false);

                    DataOutputStream wr = new DataOutputStream(httpconnect.getOutputStream());
                    wr.writeBytes(params[1]);
                    wr.flush();
                    wr.close();

                    String line;
                    String response = "";

                    try {

                        InputStream responseStream = new BufferedInputStream(httpconnect.getInputStream());
                        BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(responseStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = responseStreamReader.readLine()) != null)
                            stringBuilder.append(line);
                        responseStreamReader.close();

                        response = stringBuilder.toString();
                    } catch ( FileNotFoundException e ) {
                        Log.e("Error",e.toString());
                    }

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        // got the json object with stuff in it
                    } catch (JSONException je) {
                        Log.e("Error",je.toString());
                    }

                } catch (ProtocolException e) {
                    Log.e("Error", e.toString());
                } finally {

                    httpconnect.disconnect();

                }

            } catch (MalformedURLException e) {
                Log.e("Error", e.toString());
            }
        } catch (IOException e) {
            Log.e("Error", e.toString());
        }

        return null;
    }
}
