package com.stalled.bathroam;

import android.os.AsyncTask;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class UploadBathroomTask extends AsyncTask<String,Void,Void> {
    @Override
    protected Void doInBackground(String... params) {

        HttpURLConnection httpconnect;
        URL url;
        String content = params[1];

        try {

            url = new URL(params[0]);

            try {

                httpconnect = (HttpURLConnection) url.openConnection();
                httpconnect.setDoOutput(true);
                httpconnect.setInstanceFollowRedirects(false);

                try {

                    httpconnect.setRequestMethod("POST");
                    httpconnect.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    httpconnect.setRequestProperty("charset", "utf-8");
                    httpconnect.setUseCaches(false);
                    httpconnect.setRequestProperty("Content-Length", Integer.toString(content.length()));

                    DataOutputStream wr = new DataOutputStream(httpconnect.getOutputStream());
                    wr.write(content.getBytes());

                } catch (ProtocolException e) {
                    Log.d("Error", e.toString());
                }
            } catch (MalformedURLException e) {
                Log.d("Error", e.toString());
            }
        } catch (IOException e) {
            Log.d("Error", e.toString());
        }

        return null;
    }
}
