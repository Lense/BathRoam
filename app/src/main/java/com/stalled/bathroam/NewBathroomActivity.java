package com.stalled.bathroam;

import android.accounts.NetworkErrorException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.NetworkOnMainThreadException;
import android.support.design.widget.FloatingActionButton;
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
        final double Lat = extra.getFloat("Lat");
        final double Lon = extra.getFloat("Lon");

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

                float noveltyStars = (float) novelty.getNumStars();
                float kleeneStars = (float) cleanliness.getNumStars();

                String content = "";

                // currently available
                content += "lat="+Double.toString(Lat)+"\n";
                content += "lon="+Double.toString(Lon)+"\n";
                content += "class="+classSelected+"\n";
                content += "gender="+genderSelected+"\n";
                content += "novelty="+Float.toString(noveltyStars)+"\n";
                content += "cleanliness="+Float.toString(kleeneStars)+"\n";
                content += "floor=0\n";
                content += "public="+((privateBathroom.isChecked())?("false"):("true"))+"\n";

                // must use defaults
                content += "paper=false\n";
                content += "dryers=false\n";
                content += "stalls=0\n";
                content += "handicap=false\n";
                content += "sinks=0\n";
                content += "sanitizer=false\n";
                content += "baby=false\n";
                content += "urinals=0\n";
                content += "feminine=false\n";
                content += "medicine=false\n";
                content += "contraceptive=false\n";

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("104.131.49.58")
                        .appendPath("api")
                        .appendPath("bathrooms")
                        .appendPath("create");

                new UploadBathroomTask().execute(builder.build().toString(), content);

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
