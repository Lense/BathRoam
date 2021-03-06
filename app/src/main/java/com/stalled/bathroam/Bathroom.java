package com.stalled.bathroam;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
 *  The Bathroom class holds detailed information about a specific bathroom
 * location. The class has two states: complete and incomplete. In the complete
 * state, a Bathroom object has data on every aspect of the associated location.
 * In the incomplete state, the Bathroom object only has the ID, location and
 * ratings of the associated location.
 *
 */

public class Bathroom {
	// Necessary variables
	private int 	mID;
	private LatLng  mLocation;
	private float   mNovelty;
	private float   mCleanliness;

	// 'Complete' variables
	private String  mClass;
	private String  mGender;
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

	private boolean mComplete;

	// Empty constructor
	public Bathroom() {}

	// Incomplete state constructor
	public Bathroom(int id, LatLng location, float novelty, float cleanliness) {
		mID = id;
		mLocation = location;
		mNovelty = novelty;
		mCleanliness = cleanliness;
		mComplete = false;
	}

	// Instant complete state constructor
	public Bathroom(JSONObject details) {
		try {
			setDetails(details);
		} catch (JSONException e) {
			e.printStackTrace();
			mComplete = false;
		}
	}

	// Accessor functions
	public float getRating() {
		return (mCleanliness + mNovelty) / 2.0f;
	}

	public float getCleanliness() {
		return mCleanliness;
	}

	public float getNovelty() {
		return mNovelty;
	}

	public int getID() {
		return mID;
	}

	public LatLng getLocation() {
		return mLocation;
	}

	public boolean isComplete() {
		return mComplete;
	}

	public String getBClass() {
		return mClass;
	}
	public String  getGender() {
		return mGender;
	}
	public int getFloor() {
		return mFloor;
	}
	public boolean getPrivate() {
		return mPrivate;
	}
	public boolean getPaper() {
		return mPaper;
	}
	public boolean getDryers() {
		return mDryers;
	}
	public int getStalls() {
		return mStalls;
	}
	public boolean getHandicap() {
		return mHandicap;
	}
	public int getSinks() {
		return mSinks;
	}
	public boolean getSanitizer() {
		return mSanitizer;
	}
	public boolean getBaby() {
		return mBaby;
	}
	public int getUrinals() {
		return mUrinals;
	}
	public boolean getFeminine() {
		return mFeminine;
	}
	public boolean getMedicine() {
		return mMedicine;
	}
	public boolean getContraceptive() {
		return mContraceptive;
	}

	// Generate a bathroom object based on an incoming JSON file from server
	public void setDetails(JSONObject details) throws JSONException {
		JSONArray loc = details.getJSONArray("loc");
		mID = details.getInt("id");
		mLocation = new LatLng(loc.getDouble(0), loc.getDouble(1));
		mClass = details.getString("class");
		mGender = details.getString("gender");
		mNovelty = (float) details.getDouble("novelty");
		mCleanliness = (float) details.getDouble("cleanliness");
		mFloor = details.getInt("floor");
		mPrivate = details.getBoolean("public");
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

		mComplete = true;
	}

	// Comparison operator for the Bathroom class
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
		return c.mID == this.mID;
	}

}
