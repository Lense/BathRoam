package com.stalled.bathroam;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.RunnableFuture;

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
	private ArrayList<Bitmap> mPhotos;
	private ImageView mImageView;
	private Uri mUri;
    private final String TAG = "DrilldownActivity";
	private AnimationDrawable mAnimation;

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

	   ActionBar ab = getSupportActionBar();
	   ab.setDisplayHomeAsUpEnabled(true);
	   ab.setDisplayShowTitleEnabled(false);z

	   // Build the URL for the request
	   Uri.Builder builder = new Uri.Builder();
	   builder.scheme("http")
			 .authority("104.131.49.58")
			 .appendPath("api")
			 .appendPath("bathrooms")
			 .appendQueryParameter("id", String.valueOf(mBathroomID));
	   String url = builder.build().toString();

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
		updateImageList();
    }

    private void updateElements() {
	    if (!mBathroom.isComplete()) return;
	   RatingBar ratingClean = (RatingBar) findViewById(R.id.ratingClean);
	   RatingBar ratingNovel = (RatingBar) findViewById(R.id.ratingNovel);
	    if (ratingClean != null)
		    ratingClean.setRating(mBathroom.getCleanliness());
	    if (ratingNovel != null)
		    ratingNovel.setRating(mBathroom.getNovelty());

	    TextView ratingOverall = (TextView) findViewById(R.id.ratingOverall);
	    if (ratingOverall != null)
		    ratingOverall.setText(String.format("%.1f", mBathroom.getRating()));


	    ImageView gender = (ImageView) findViewById(R.id.aigaMale);
	    if (gender != null)
		    gender.setImageResource(mBathroom.getGender().equals("Male") ? R.drawable.aiga_men_select : R.drawable.aiga_men);
	    gender = (ImageView) findViewById(R.id.aigaFemale);
	    if (gender != null)
		    gender.setImageResource(mBathroom.getGender().equals("Female") ? R.drawable.aiga_women_select : R.drawable.aiga_women);
	    gender = (ImageView) findViewById(R.id.aigaUni);
	    if (gender != null)
		    gender.setImageResource(mBathroom.getGender().equals("Unisex") ? R.drawable.aiga_unisex_select : R.drawable.aiga_unisex);

	    TextView text = (TextView) findViewById(R.id.drilldownPrivate);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getPrivate() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getPrivate() ? Typeface.BOLD : Typeface.NORMAL);
	    }


	    text = (TextView) findViewById(R.id.drilldownPaper);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getPaper() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getPaper() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownDryers);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getDryers() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getDryers() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownHandicap);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getHandicap() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getHandicap() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownSanitizer);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getSanitizer() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getSanitizer() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownBaby);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getBaby() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getBaby() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownFeminine);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getFeminine() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getFeminine() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownMedicine);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getMedicine() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getMedicine() ? Typeface.BOLD : Typeface.NORMAL);
	    }

	    text = (TextView) findViewById(R.id.drilldownContraceptive);
	    if (text != null) {
		    text.setTextColor(getResources().getColor(mBathroom.getContraceptive() ? R.color.colorPresent : R.color.colorAbsent));
		    text.setTypeface(null, mBathroom.getContraceptive() ? Typeface.BOLD : Typeface.NORMAL);
	    }

		mImageView = (ImageView) findViewById(R.id.drilldownImage);
    }

	public void takePhoto( View view ) {
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraIntent, 0);
	}

	public void submitRating( View view ) {
		DialogFragment newFragment = new RatingDialog();
		Bundle args = new Bundle();
		args.putString("id", Integer.toString(mBathroomID));
		newFragment.setArguments(args);
		newFragment.show(getFragmentManager(), "bathroamRating");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK)
            switch (requestCode) {n
			  case 0:
				  Bundle extras = data.getExtras();
				  Bitmap imageBitmap = (Bitmap) extras.get("data");
				  ByteArrayOutputStream stream = new ByteArrayOutputStream();
				  if (imageBitmap != null)
					  imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
				  byte[] byteArray = stream.toByteArray();
				  String result = Base64.encodeToString(byteArray, Base64.URL_SAFE);
				  new UploadBathroomTask().execute("http://toilets.lense.su/api/bathrooms/images/create",
						  "image="+result+"&bathroom_id="+Integer.toString(mBathroom.getID()));
				  break;
			  default:
				  break;
		  }
	}

	private void updateImageList() {
		// Build the URL for the request
		mPhotos = new ArrayList<>();
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("http")
				.authority("104.131.49.58")
				.appendPath("api")
				.appendPath("bathrooms")
				.appendPath(String.valueOf(mBathroomID))
				.appendPath("images");
		String url = builder.build().toString();

		Log.d(TAG, url);

		// Request bathroom details
		JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				for (int i = 0; i < response.length(); i++) {
					try {
						// Parse the JSON object for bathroom information
						JSONObject meta = response.getJSONObject(i);
						byte[] raw = Base64.decode(meta.getString("image"), Base64.URL_SAFE);
						Bitmap image = BitmapFactory.decodeByteArray(raw, 0, raw.length);
						mPhotos.add(image);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				if (mImageView != null && mPhotos.size() > 0) {
					startAnimation();
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				Toast.makeText(getApplicationContext(), "Failed to retrieve images.", Toast.LENGTH_SHORT).show();
				Log.w(TAG, error);
			}
		});
		RequestHandler.getInstance().addToReqQueue(request, "jreq", getApplicationContext());
	}

	class Animator implements Runnable {
		public void run() {
			mAnimation.start();
		}
	}

	private void startAnimation() {
		mAnimation = new AnimationDrawable();
		for (int i=0; i < mPhotos.size(); i++)
			mAnimation.addFrame(new BitmapDrawable(mPhotos.get(i)), 8000);
		mAnimation.setEnterFadeDuration(1000);
		mAnimation.setExitFadeDuration(1000);
		mAnimation.setOneShot(false);
		mImageView.setImageDrawable(mAnimation);
		mImageView.post(new Animator());
	}
}
