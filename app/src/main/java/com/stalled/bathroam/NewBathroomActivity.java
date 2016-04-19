package com.stalled.bathroam;

import android.accounts.NetworkErrorException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.io.DataOutputStream;

public class NewBathroomActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extra = getIntent().getExtras();
        final double Lat = extra.getDouble("lat");
        final double Lon = extra.getDouble("lon");

        setContentView(R.layout.activity_new_bathroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup classGroup = (RadioGroup) findViewById(R.id.ClassGroup);
                RadioGroup genderGroup = (RadioGroup) findViewById(R.id.GenderGroup);
                RatingBar novelty = (RatingBar) findViewById(R.id.noveltyRater);
                RatingBar cleanliness = (RatingBar) findViewById(R.id.cleanRater);

                CheckBox privateBathroom = (CheckBox) findViewById(R.id.privateBathroom);

                String classSelected = "multi";
                String genderSelected = "neutral";

                int classid = classGroup.getCheckedRadioButtonId();
                if ( classid == R.id.multiPerson )
                    classSelected = "multi";
                else if ( classid == R.id.singleOccupancy)
                    classSelected = "single";
                else if ( classid == R.id.portableRestroom )
                    classSelected = "portable";
                else if ( classid == R.id.pitToilet )
                    classSelected = "pit";

                int genderid = genderGroup.getCheckedRadioButtonId();
                if ( genderid == R.id.genderMale )
                    genderSelected = "male";
                else if ( genderid == R.id.genderFemale )
                    genderSelected = "female";
                else if ( genderid == R.id.genderNeutral )
                    genderSelected = "neutral";

                float noveltyStars = novelty.getRating();
                float kleeneStars = cleanliness.getRating();

                String content = "";

                // currently available
                content += "lat="+Double.toString(Lat)+"&";
                content += "lon="+Double.toString(Lon)+"&";
                content += "class="+classSelected+"&";
                content += "gender="+genderSelected+"&";
                content += "novelty="+Float.toString(noveltyStars)+"&";
                content += "cleanliness="+Float.toString(kleeneStars)+"&";
                content += "floor=0&";
                content += "public="+((privateBathroom.isChecked())?("False"):("True"))+"&";

                // must use defaults
                content += "paper=False&";
                content += "dryers=False&";
                content += "stalls=0&";
                content += "handicap=False&";
                content += "sinks=0&";
                content += "sanitizer=False&";
                content += "baby=False&";
                content += "urinals=0&";
                content += "feminine=False&";
                content += "medicine=False&";
                content += "contraceptive=False";

                new UploadBathroomTask().execute("http://toilets.lense.su/api/bathrooms/create", content);

                setResult(RESULT_OK);
                NewBathroomActivity.this.finish();
            }
        });

        ActionBar ab = getSupportActionBar();
        if ( ab != null ) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        }
    }

}
