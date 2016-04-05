package com.stalled.bathroam;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SeekBar min_rating_slider;
    private float min_rating;
    private Marker mMarker;
    private GoogleApiClient client;
    private ArrayList<Bathroom> local_bathrooms = new ArrayList<Bathroom>();

    // Will added this
    private boolean DepartedForNewBathroom = false;

    private static final String TAG = "MapActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar mActionBar = getSupportActionBar();
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.slider, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);

        min_rating = 0;
        min_rating_slider = (SeekBar) findViewById(R.id.rating);
        min_rating_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                min_rating = (float)progress/10;
                mMap.clear();
                for(int i=0; i < local_bathrooms.size(); i++) {
                    Bathroom this_bathroom = local_bathrooms.get(i);
                    if(this_bathroom.getRating() > min_rating){
                        mMarker = mMap.addMarker(new MarkerOptions().position(this_bathroom.getLocation()).title(String.valueOf(this_bathroom.getRating())));
                    }
                }
                TextView textView = (TextView) findViewById(R.id.rating_value);
                textView.setText(String.valueOf(min_rating));
            }

        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DepartedForNewBathroom = true;
                Intent intent1 = new Intent(MainActivity.this, NewBathroomActivity.class);
                startActivity(intent1);
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Create a sample marker when the map is ready
        LatLng RPI = new LatLng(42.730160, -73.678814);
        mMarker = mMap.addMarker(new MarkerOptions().position(RPI).title("RPI"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(RPI, 18));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent1 = new Intent(MainActivity.this, DrilldownActivity.class);
                String title = marker.getTitle();
                intent1.putExtra("markertitle", title);
                startActivity(intent1);
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                // Build the URL for the request
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("104.131.49.58")
                        .appendPath("api")
                        .appendPath("bathrooms")
                        .appendPath("nearby")
                        .appendQueryParameter("ne_lat", String.valueOf(bounds.northeast.latitude))
                        .appendQueryParameter("ne_long", String.valueOf(bounds.northeast.longitude))
                        .appendQueryParameter("sw_lat", String.valueOf(bounds.southwest.latitude))
                        .appendQueryParameter("sw_long", String.valueOf(bounds.southwest.longitude));
                String url = builder.build().toString();
                GetLocalBathrooms(url);

            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.stalled.bathroam/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.stalled.bathroam/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if ( DepartedForNewBathroom ) {
            DepartedForNewBathroom = false;
            Snackbar.make(findViewById(R.id.fab),
                    "Thank you for your submission!",
                    Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    private void GetLocalBathrooms(String url) {

        JsonArrayRequest jsArrReq = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>(){
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject jo = response.getJSONObject(i);
                        int id = jo.getInt("id");
                        JSONArray location = jo.getJSONArray("loc");
                        Double lat = (Double)location.get(0);
                        Double lon = (Double)location.get(1);
                        Double rating = jo.getDouble("rating");
                        Bathroom new_bathroom = new Bathroom(id, lat, lon, rating);
                        if(rating > min_rating && !local_bathrooms.contains(new_bathroom)) {
                            local_bathrooms.add(new_bathroom);
                            mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title(String.valueOf(rating)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, error);
            }
        });

        RequestHandler.getInstance().addToReqQueue(jsArrReq, "jreq", getApplicationContext());
    }


}
