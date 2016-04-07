package com.stalled.bathroam;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

public class NewBathroomActivity extends AppCompatActivity {
    private float Lat;
    private float Lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = getIntent().getExtras();
        Lat = extra.getFloat("Lat");
        Lon = extra.getFloat("Lon");

        setContentView(R.layout.activity_new_bathroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject obj = new JSONObject();

                NewBathroomActivity.this.finish();
            }
        });

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("Hello", "Could not generate back button. Sorry :(");
        }
    }

}
