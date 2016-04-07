package com.stalled.bathroam;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Bathroom {
    private int     mId;
    private LatLng  mLocation;
    private float   mRating;
    private String  mClass;
    private String  mGender;
    private float   mNovelty;
    private float   mCleanliness;
    private int     mFloor;
    private boolean mPrivate;
    private boolean mPaper;
    private boolean mDryers;
    private int     mStalls;
    private boolean mHandicap;
    private int     mSinks;
    private boolean mSanitizer;
    private boolean mBaby;
    private int     mUrinals;
    private boolean mFeminine;
    private boolean mMedicine;
    private boolean mContraceptive;
    private boolean mUrinal;

    private boolean mComplete;

    public Bathroom() {}

    public Bathroom(int id, LatLng location, float rating) {
        mId = id;
        mLocation = location;
        mRating = rating;

        mComplete = false;
    }

    public Bathroom(JSONObject details) {
        try {
            setDetails(details);
        } catch (JSONException e) {
            e.printStackTrace();
            mComplete = false;
        }
    }

    public float getRating() {
        if (mComplete) {
            mRating = (mCleanliness + mNovelty) / 2;
        }
        return mRating;
    }

    public float getCleanliness() {
        return mCleanliness;
    }

    public float getNovelty() {
        return mNovelty;
    }

    public int getID() {
        return mId;
    }

    public LatLng getLocation() {
        return mLocation;
    }

    public boolean isComplete() {
        return mComplete;
    }

    public void setDetails(JSONObject details) throws JSONException {

        JSONArray loc = details.getJSONArray("loc");
        mId = details.getInt("id");
        mLocation = new LatLng(loc.getDouble(0), loc.getDouble(1));
        mRating = (float) details.getDouble("rating");
        mClass = details.getString("class");
        mGender = details.getString("gender");
        mNovelty = (float) details.getDouble("novelty");
        mCleanliness = (float) details.getDouble("cleanliness");
        mFloor = details.getInt("floor");
        mPrivate = details.getBoolean("private");
        mPaper = details.getBoolean("paper");
        mDryers = details.getBoolean("dryers");
        mStalls = details.getInt("stalls");
        mHandicap = details.getBoolean("handicap");
        mSinks = details.getInt("sinks");
        mSanitizer = details.getBoolean("sanitizer");
        mBaby = details.getBoolean("baby");
        mUrinals = details.getInt("urinals");
        mFeminine = details.getBoolean("feminine");
        mMedicine = details.getBoolean("medicine");
        mContraceptive = details.getBoolean("contraceptive");
        mUrinal = details.getBoolean("urinal");

        mComplete = true;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Bathroom)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Bathroom c = (Bathroom) o;

        // Compare the data members and return accordingly
        return c.mId == this.mId;
    }

}
