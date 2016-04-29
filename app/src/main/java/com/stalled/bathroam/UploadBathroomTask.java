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

// Takes in a string to upload to the server in the form of POST data
public class UploadBathroomTask extends AsyncTask<String,Void,Void> {
	@Override
	protected Void doInBackground(String... params) {

		// Params[0] = URL to send to

		HttpURLConnection httpConnect;
		URL url;

		try {

			url = new URL(params[0]);

			try {

				// Set up the connection
				httpConnect = (HttpURLConnection) url.openConnection();
				httpConnect.setDoOutput(true);
				httpConnect.setDoInput(true);
				httpConnect.setInstanceFollowRedirects(false);
				httpConnect.setChunkedStreamingMode(0);

				try {

					httpConnect.setRequestMethod("POST");
					httpConnect.setUseCaches(false);

					// Write params[1] to the output stream
					DataOutputStream wr = new DataOutputStream(httpConnect.getOutputStream());
					wr.writeBytes(params[1]);
					wr.flush();
					wr.close();

					String line;
					String response = "";

					try {

						// Get response from server
						InputStream responseStream = new BufferedInputStream(httpConnect.getInputStream());
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
						// Got the json object with stuff in it
					} catch (JSONException je) {
						Log.e("Error",je.toString());
					}

				} catch (ProtocolException e) {
					Log.e("Error", e.toString());
				} finally {

					httpConnect.disconnect();

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
