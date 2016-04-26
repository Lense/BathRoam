package com.stalled.bathroam;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.Rating;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.zip.Inflater;

/*
*
* I need:
*   bathroom id, overall rating, image stream, class, location, gender, novelty rating,
*   cleanliness rating, floor, private, paper towels, dryers, stalls, handicapped stalls,
*   sinks, hand sanitizer, baby changing, urinals, feminine products, medicine vending,
*   contraceptive vending, urinal dividers, trough
*
*   as a JSON map please.
*
*/

public class DrilldownActivity extends AppCompatActivity {

    private int mBathroomID;
    private String mTitle;
    private Bathroom mBathroom;
    private final String TAG = "DrilldownActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	   super.onCreate(savedInstanceState);
	   setContentView(R.layout.activity_drilldown);

	   // Init variables
	   mBathroom = new Bathroom();
	   mTitle = "";

	   // Get intent parameters
	   Intent intent = getIntent();
	   mTitle = intent.getStringExtra("markerTitle");
	   mBathroomID = intent.getIntExtra("bathroomID", 0);

	   // Set up toolbar
	   Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
	   setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
	   ActionBar ab = getSupportActionBar();
	   ab.setDisplayHomeAsUpEnabled(true);
	   ab.setDisplayShowTitleEnabled(false);

	   // Build the URL for the request
	   Uri.Builder builder = new Uri.Builder();
	   builder.scheme("http")
			 .authority("104.131.49.58")
			 .appendPath("api")
			 .appendPath("bathrooms")
			 .appendQueryParameter("id", String.valueOf(mBathroomID));
	   String url = builder.build().toString();
	   Log.d("Hello",url);

	   // Request bathroom details
	   JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
		  @Override
		  public void onResponse(JSONObject response) {
			 try {
				// Parse the JSON object for bathroom information
				mBathroom.setDetails(response);
				updateElements();
			 } catch (JSONException e) {
				e.printStackTrace();
			 }
		  }
	   }, new Response.ErrorListener() {
		  @Override
		  public void onErrorResponse(VolleyError error) {
			 Log.w(TAG, error);
		  }
	   });

	   RequestHandler.getInstance().addToReqQueue(request, "jreq", getApplicationContext());
    }

    private void updateElements() {
	    if (!mBathroom.isComplete()) return;
	   Log.d(TAG, "update");
	   RatingBar ratingClean = (RatingBar) findViewById(R.id.ratingClean);
	   RatingBar ratingNovel = (RatingBar) findViewById(R.id.ratingNovel);
	   ratingClean.setRating(mBathroom.getCleanliness());
	   ratingNovel.setRating(mBathroom.getNovelty());

	   TextView ratingOverall = (TextView) findViewById(R.id.ratingOverall);
	   ratingOverall.setText(String.format("%.1f", mBathroom.getRating()));

	    ImageView gender = (ImageView) findViewById(R.id.aigaMale);
	    Log.d(TAG, mBathroom.getGender());
	    gender.setColorFilter( mBathroom.getGender().equals("male") ? R.color.colorPresent : R.color.colorAbsent, PorterDuff.Mode.MULTIPLY);
	    gender = (ImageView) findViewById(R.id.aigaFemale);
	    gender.setColorFilter( mBathroom.getGender().equals("female") ? R.color.colorPresent : R.color.colorAbsent, PorterDuff.Mode.MULTIPLY);
	    gender = (ImageView) findViewById(R.id.aigaUni);
	    gender.setColorFilter( mBathroom.getGender().equals("neutral") ? R.color.colorPresent : R.color.colorAbsent, PorterDuff.Mode.MULTIPLY);

	    TextView text = (TextView) findViewById(R.id.drilldownPrivate);
	    text.setTextColor(mBathroom.getPrivate() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownPaper);
	    text.setTextColor(mBathroom.getPaper() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownDryers);
	    text.setTextColor(mBathroom.getDryers() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownHandicap);
	    text.setTextColor(mBathroom.getHandicap() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownSanitizer);
	    text.setTextColor(mBathroom.getSanitizer() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownBaby);
	    text.setTextColor(mBathroom.getBaby() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownFeminine);
	    text.setTextColor(mBathroom.getFeminine() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownMedicine);
	    text.setTextColor(mBathroom.getMedicine() ? R.color.colorPresent : R.color.colorAbsent);
	    text = (TextView) findViewById(R.id.drilldownContraceptive);
	    text.setTextColor(mBathroom.getContraceptive() ? R.color.colorPresent : R.color.colorAbsent);

    }
}
