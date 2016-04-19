package com.stalled.bathroam;

import android.os.Bundle;

public class PreferenceFragment extends android.preference.PreferenceFragment {

	public PreferenceFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);

		addPreferencesFromResource(R.xml.filter_preferences);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
}