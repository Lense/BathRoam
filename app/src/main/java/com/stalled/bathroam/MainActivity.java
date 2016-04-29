package com.stalled.bathroam;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements
    OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    // FOR THE PREFERENCE DRAWER
    private DrawerLayout mDrawerLayout;

    private float mMinRating;
    private ArrayList<Bathroom> mLocalBathrooms = new ArrayList<Bathroom>();
    private GoogleMap mMap;

	private com.stalled.bathroam.PreferenceDrawerFragment mPreferenceDrawerFragment;
	private ActionBarDrawerToggle mDrawerToggle;
	private LinearLayout mToolbar;
    private SharedPreferences mPreferences;

    private GoogleApiClient mClient;
    private static final String TAG = "MapActivity";
    private LatLng mLastLocation;
    private Bathroom mNearestBathroom;
    private HashMap<String, Bathroom> mBathroomMap;

    // private members indicating the current preferences
    private String mPrefClass;
    private String mPrefGender;
    private boolean mPrefNovelty;
    private boolean mPrefCleanliness;
    private boolean mPrefPrivate;
    private boolean mPrefPaper;
    private boolean mPrefDryers;
    private boolean mPrefHandicap;
    private boolean mPrefSanitizer;
    private boolean mPrefBaby;
    private boolean mPrefFeminine;
    private boolean mPrefMedicine;
    private boolean mPrefContraceptive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

	    // Populate the Preference Drawer with the preference fragment
	    mPreferenceDrawerFragment = (PreferenceDrawerFragment) getFragmentManager().findFragmentById(R.id.preference_drawer);
	    mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
	    mPreferenceDrawerFragment.setUp( R.id.preference_drawer, mDrawerLayout);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        updatePreferences();

	    mDrawerToggle = new ActionBarDrawerToggle( this, mDrawerLayout, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
		    public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                
                // Reset the map with the new preferences
                updatePreferences();
                mMap.clear();
                mLocalBathrooms.clear();
                mBathroomMap.clear();
                getLocalBathrooms();
				if (mPrefCleanliness || mPrefNovelty)
					hideRatingBar( true );
				else
					hideRatingBar( false );
		    }

		    public void onDrawerOpened(View drawerView) {
			    super.onDrawerOpened(drawerView);
		    }
	    };

	    mDrawerLayout.setDrawerListener(mDrawerToggle);
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setHomeButtonEnabled(true);

	    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
	    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.map);
	    mapFragment.getMapAsync(this);

		// Mark the toolbar for use later
		mToolbar = (LinearLayout) findViewById(R.id.rating_bar);

		// Initialize the SeekBar listener for minimum bathroom ratings
		mMinRating = 0;
		SeekBar minRatingSlider = (SeekBar) findViewById(R.id.rating);
        if(minRatingSlider != null){
            minRatingSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    // Nothing to do here
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    // Nothing to do here
                }

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // Update the displayed minimum rating in the layout
                    mMinRating = (float) progress / 10;
                    ((TextView) findViewById(R.id.rating_value)).setText(String.valueOf(mMinRating));

                    // Only display bathrooms meeting the minimum rating requirement
                    mMap.clear();
                    for (int i = 0; i < mLocalBathrooms.size(); i++) {
                        Bathroom bathroom = mLocalBathrooms.get(i);

                        float rating;
                        if((mPrefNovelty && mPrefCleanliness) || (!mPrefNovelty && !mPrefCleanliness)){
                            rating = bathroom.getRating();
                        }else if(mPrefNovelty){
                            rating = bathroom.getNovelty();
                        }else{
                            rating = bathroom.getCleanliness();
                        }

                        if (rating >= mMinRating) {
                            mMap.addMarker(new MarkerOptions()
                                    .position(bathroom.getLocation())
                                    .title(String.valueOf(rating))
                            );
                        }
                    }
                }
            });
        }

        // Build the GoogleAPIClient
        buildGoogleApiClient();
        if (mClient != null) {
            mClient.connect();
        } else { Toast.makeText(this, "Not connected...", Toast.LENGTH_SHORT).show(); }

        // Set the intent for the new bathroom FAB
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.new_bathroom_fab);

        assert fab1 != null;
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, NewBathroomActivity.class);

                if (mLastLocation == null) {
                    Toast.makeText(getApplicationContext(), "Cannot find your location!", Toast.LENGTH_LONG).show();
                    intent1.putExtra("lat", 0.0);
                    intent1.putExtra("lon", 0.0);
                } else {
                    intent1.putExtra("lat", mLastLocation.latitude);
                    intent1.putExtra("lon", mLastLocation.longitude);
                }

                startActivityForResult(intent1, 0);
            }
        });

        // Set the intent for the find nearest bathroom FAB
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.nearest_bathroom_fab);
        if(fab2 != null) {
            fab2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getNearestBathroom();
                }
            });
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mBathroomMap = new HashMap<>();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request the permissions
        } else {
            Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
            mMap.setMyLocationEnabled(true);
            if (myLocation != null) {
                mLastLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, 18));
                Log.d(TAG, "AT "+mLastLocation.toString());
            } else {Log.d(TAG, "COULDN'T LOCATE");}
        }

        // Open the bathroom drilldown when a marker is clicked
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent1 = new Intent(MainActivity.this, DrilldownActivity.class);
                String title = marker.getTitle();
                intent1.putExtra("markerTitle", title);
                try {

                    intent1.putExtra("bathroomID", mBathroomMap.get(marker.getId()).getID());
                } catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), "Unable to display bathroom", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent1);
            }
        });

        // Check for more bathrooms when the camera is moved
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                getLocalBathrooms();
            }
        });

    }

    private void getLocalBathrooms() {

        // Build the URL for the request
        LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("104.131.49.58")
                .appendPath("api")
                .appendPath("bathrooms")
                .appendPath("within")
                .appendQueryParameter("ne_lat", String.valueOf(bounds.northeast.latitude))
                .appendQueryParameter("ne_lon", String.valueOf(bounds.northeast.longitude))
                .appendQueryParameter("sw_lat", String.valueOf(bounds.southwest.latitude))
                .appendQueryParameter("sw_lon", String.valueOf(bounds.southwest.longitude))
                .appendQueryParameter("gender", mPrefGender)
                .appendQueryParameter("class", mPrefClass)
                .appendQueryParameter("public", String.valueOf(mPrefPrivate))
                .appendQueryParameter("paper", String.valueOf(mPrefPaper))
                .appendQueryParameter("dryers", String.valueOf(mPrefDryers))
                .appendQueryParameter("handicap", String.valueOf(mPrefHandicap))
                .appendQueryParameter("sanitizer", String.valueOf(mPrefSanitizer))
                .appendQueryParameter("baby", String.valueOf(mPrefBaby))
                .appendQueryParameter("feminine", String.valueOf(mPrefFeminine))
                .appendQueryParameter("medicine", String.valueOf(mPrefMedicine))
                .appendQueryParameter("contraceptive", String.valueOf(mPrefContraceptive));
        String url = builder.build().toString();

        Log.d(TAG, url);

        JsonArrayRequest jsArrReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    try {
                        // Parse the JSON object for bathroom information
                        JSONObject jo = response.getJSONObject(i);
                        int id = jo.getInt("id");
                        JSONArray locArray = jo.getJSONArray("loc");
                        Double lat = Double.parseDouble((String)locArray.get(0));
                        Double lon = Double.parseDouble((String)locArray.get(1));
                        LatLng loc = new LatLng(lat, lon);
                        float novelty = (float) jo.getDouble("novelty");
                        float cleanliness = (float) jo.getDouble("cleanliness");

                        // Construct the new bathroom and add it if necessary
                        Bathroom newBathroom = new Bathroom(id, loc, novelty, cleanliness);
                        if(!mLocalBathrooms.contains(newBathroom)){
                            mLocalBathrooms.add(newBathroom);

                            float rating;
                            if((mPrefNovelty && mPrefCleanliness) || (!mPrefNovelty && !mPrefCleanliness)){
                                rating = (novelty + cleanliness)/2.0f;
                            }else if(mPrefNovelty){
                                rating = novelty;
                            }else{
                                rating = cleanliness;
                            }

                            if(rating >= mMinRating){
                                Marker tmp = mMap.addMarker(new MarkerOptions().position(loc).title(String.format("%.1f", rating)));
                                mBathroomMap.put(tmp.getId(), newBathroom);
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();
                Log.w(TAG, error);
            }
        });
        RequestHandler.getInstance().addToReqQueue(jsArrReq, "jreq", getApplicationContext());
    }

    private void getNearestBathroom() {

        // Build the URL for the request
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http")
                .authority("104.131.49.58")
                .appendPath("api")
                .appendPath("bathrooms")
                .appendPath("nearest");
                if (mLastLocation != null) {
                    builder.appendQueryParameter("lat", String.valueOf(mLastLocation.latitude))
                    .appendQueryParameter("lon", String.valueOf(mLastLocation.longitude));
                } else {
                    builder.appendQueryParameter("lat", "0")
                    .appendQueryParameter("lon", "0");
                }
        String url = builder.build().toString();

        JsonObjectRequest jsObjReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    // Parse the JSON object for bathroom information
                    int id = response.getInt("id");
                    JSONArray locArray = response.getJSONArray("loc");
                    Double lat = Double.parseDouble((String) locArray.get(0));
                    Double lon = Double.parseDouble((String) locArray.get(1));
                    LatLng loc = new LatLng(lat, lon);
                    float novelty = (float) response.getDouble("novelty");
                    float cleanliness = (float) response.getDouble("cleanliness");

                    mNearestBathroom = new Bathroom(id, loc, novelty, cleanliness);
                    if(!mLocalBathrooms.contains(mNearestBathroom)){
                        mLocalBathrooms.add(mNearestBathroom);
                    }

                    float rating;
                    if((mPrefNovelty && mPrefCleanliness) || (!mPrefNovelty && !mPrefCleanliness)){
                        rating = (novelty + cleanliness)/2.0f;
                    }else if(mPrefNovelty){
                        rating = novelty;
                    }else{
                        rating = cleanliness;
                    }

                    Marker tmp = mMap.addMarker(new MarkerOptions().position(loc).title(String.format("%.1f", rating)));
                    mBathroomMap.put(tmp.getId(), mNearestBathroom);
                    CameraPosition newCamPos = new CameraPosition(loc,
                            mMap.getCameraPosition().zoom,
                            mMap.getCameraPosition().tilt, //use old tilt
                            mMap.getCameraPosition().bearing); //use old bearing
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 250, null);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to locate nearest restroom!", Toast.LENGTH_LONG).show();
                    mNearestBathroom = null;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Failed to connect...", Toast.LENGTH_SHORT).show();
                Log.w(TAG, error);
            }
        });
        RequestHandler.getInstance().addToReqQueue(jsObjReq, "jreq", getApplicationContext());
    }

    // set elements of the preference drawer
    private void updatePreferences(){
        mPrefClass = mPreferences.getString("class", "none");
        mPrefGender = mPreferences.getString("gender", "none");
        mPrefNovelty = mPreferences.getBoolean("novelty", false);
        mPrefCleanliness = mPreferences.getBoolean("cleanliness", false);
        mPrefPrivate = mPreferences.getBoolean("private", false);
        mPrefPaper = mPreferences.getBoolean("paper", false);
        mPrefDryers = mPreferences.getBoolean("dryers", false);
        mPrefHandicap = mPreferences.getBoolean("handicap", false);
        mPrefSanitizer = mPreferences.getBoolean("sanitizer", false);
        mPrefBaby = mPreferences.getBoolean("baby", false);
        mPrefFeminine = mPreferences.getBoolean("feminine", false);
        mPrefMedicine = mPreferences.getBoolean("medicine", false);
        mPrefContraceptive = mPreferences.getBoolean("contraceptive", false);
		if (mPrefCleanliness || mPrefNovelty)
			hideRatingBar( true );
		else
			hideRatingBar( false );

    }

    protected synchronized void buildGoogleApiClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle arg0) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // request the permissions
            return;
        }

        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);
        if (myLocation != null) {
            mLastLocation = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLastLocation, 18));
            Log.d(TAG, "AT "+mLastLocation.toString());
        } else {Log.d(TAG, "COULDNT LOCATE");}

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		int id = item.getItemId();
		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case 0: // receive data returning from NewBathroomActivity
                    try {
                        Toast.makeText(this, "Thank you for your submission!",
                                Toast.LENGTH_LONG).show();
                    } catch (NullPointerException e) {
                        Log.d("Could not display", e.toString());
                    }
                    break;
            }
    }

	private void hideRatingBar( boolean b ) {
		if (mToolbar != null)
			mToolbar.setScaleY(b ? 1 : 0);
	}

}
