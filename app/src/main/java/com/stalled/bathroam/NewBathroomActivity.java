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
import android.widget.NumberPicker;
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

        int[] ids = new int[]{
                R.id.floorNumberPicker,
                R.id.sinksNumberPicker,
                R.id.stallsNumberPicker,
                R.id.urinalsNumberPicker
        };
        for (int id : ids ) {
            NumberPicker np = (NumberPicker) findViewById(id);
            assert np != null;
            np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            np.setMaxValue(1000);
            np.setMinValue(1);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RadioGroup classGroup = (RadioGroup) findViewById(R.id.ClassGroup);
                RadioGroup genderGroup = (RadioGroup) findViewById(R.id.GenderGroup);
                RatingBar novelty = (RatingBar) findViewById(R.id.noveltyRater);
                RatingBar cleanliness = (RatingBar) findViewById(R.id.cleanRater);

                CheckBox privateBathroom = (CheckBox) findViewById(R.id.privateBathroom);
                CheckBox paperTowelsAvailable = (CheckBox) findViewById(R.id.paperTowelBox);
                CheckBox dryersAvailable = (CheckBox) findViewById(R.id.dryersBox);
                CheckBox handicapFriendly = (CheckBox) findViewById(R.id.handicapBox);
                CheckBox sanitizerAvailable = (CheckBox) findViewById(R.id.sanitizerBox);
                CheckBox changingTableAvailable = (CheckBox) findViewById(R.id.changingBox);
                CheckBox tamponsAndPadsAvailable = (CheckBox) findViewById(R.id.feminineBox);
                CheckBox medicineAvailable = (CheckBox) findViewById(R.id.medicineBox);
                CheckBox contraceptionAvailable = (CheckBox) findViewById(R.id.contraceptionBox);

                NumberPicker floorPicker = (NumberPicker) findViewById(R.id.floorNumberPicker);
                NumberPicker stallPicker = (NumberPicker) findViewById(R.id.stallsNumberPicker);
                NumberPicker sinksPicker = (NumberPicker) findViewById(R.id.sinksNumberPicker);
                NumberPicker urinalPicker = (NumberPicker) findViewById(R.id.urinalsNumberPicker);

                String classSelected = "multi";
                String genderSelected = "neutral";

                assert classGroup != null;
                int classid = classGroup.getCheckedRadioButtonId();
                if ( classid == R.id.multiPerson )
                    classSelected = "multi";
                else if ( classid == R.id.singleOccupancy)
                    classSelected = "single";
                else if ( classid == R.id.portableRestroom )
                    classSelected = "portable";
                else if ( classid == R.id.pitToilet )
                    classSelected = "pit";

                assert genderGroup != null;
                int genderid = genderGroup.getCheckedRadioButtonId();
                if ( genderid == R.id.genderMale )
                    genderSelected = "male";
                else if ( genderid == R.id.genderFemale )
                    genderSelected = "female";
                else if ( genderid == R.id.genderNeutral )
                    genderSelected = "neutral";

                assert novelty != null;
                float noveltyStars = novelty.getRating();
                assert cleanliness != null;
                float kleeneStars = cleanliness.getRating();

                boolean privatePublic = privateBathroom.isChecked();
                boolean paper = paperTowelsAvailable.isChecked();
                boolean dryers = dryersAvailable.isChecked();
                boolean handicap = handicapFriendly.isChecked();
                boolean sanitizer = sanitizerAvailable.isChecked();
                boolean baby = changingTableAvailable.isChecked();
                boolean feminine = tamponsAndPadsAvailable.isChecked();
                boolean medicine = medicineAvailable.isChecked();
                boolean contraceptive = contraceptionAvailable.isChecked();

                int floor = floorPicker.getValue();
                int stalls = stallPicker.getValue();
                int sinks = sinksPicker.getValue();
                int urinals = urinalPicker.getValue();

                String content = "lat="+Double.toString(Lat)+"&";
                content += "lon="+Double.toString(Lon)+"&";
                content += "class="+classSelected+"&";
                content += "gender="+genderSelected+"&";
                content += "novelty="+Float.toString(noveltyStars)+"&";
                content += "cleanliness="+Float.toString(kleeneStars)+"&";
                content += "floor="+Integer.toString(floor)+"&";
                content += "public="+((privatePublic)?("False"):("True"))+"&";
                content += "paper="+((paper)?"True":"False")+"&";
                content += "dryers="+((dryers)?"True":"False")+"&";
                content += "stalls="+Integer.toString(stalls)+"&";
                content += "handicap="+((handicap)?"True":"False")+"&";
                content += "sinks="+Integer.toString(sinks)+"&";
                content += "sanitizer="+((sanitizer)?"True":"False")+"&";
                content += "baby="+((baby)?"True":"False")+"&";
                content += "urinals="+Integer.toString(urinals)+"&";
                content += "feminine="+((feminine)?"True":"False")+"&";
                content += "medicine="+((medicine)?"True":"False")+"&";
                content += "contraceptive="+((contraceptive)?"True":"False")+"";

                new UploadBathroomTask().execute("http://toilets.lense.su/api/bathrooms/create", content);

                setResult(RESULT_OK);
                NewBathroomActivity.this.finish();
            }
        });

        try {
            ActionBar ab = getSupportActionBar();
            assert ab != null;
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowHomeEnabled(true);
        } catch (NullPointerException e) {
            Log.d("Hello", "Could not generate back button. Sorry :(");
        }
    }

}
