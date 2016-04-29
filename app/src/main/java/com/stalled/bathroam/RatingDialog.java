package com.stalled.bathroam;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RatingBar;
import android.widget.Toast;

public class RatingDialog extends DialogFragment {
	String mBathroomID;
	RatingBar mNovelty;
	RatingBar mCleanliness;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.fragment_dialog, null);
		builder.setView(view);

		mBathroomID = getArguments().getString("id");
		// Grab hold of the rating bars
		mNovelty = (RatingBar) view.findViewById(R.id.rateNovel);
		mCleanliness = (RatingBar) view.findViewById(R.id.rateClean);

		// "CLICKY CLICKY OKAY BUTTON"
		builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//do the thing
				String content = "bathroom_id="+mBathroomID+"&";
				content += "mac_address="+Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID)+"&";
				content += "novelty="+ Float.toString(mNovelty.getRating())+"&";
				content += "cleanliness="+ Float.toString(mCleanliness.getRating());
				new UploadBathroomTask().execute("http://toilets.lense.su/api/ratings/create", content);
				Toast.makeText(getActivity(), "Rating submitted!", Toast.LENGTH_LONG).show();
			}
		});

		// "CLICKY CLICKY BYE-BYE BUTTON"
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Canceled dialogue
			}
		});
		builder.setTitle("Submit Rating");

		return builder.create();
	}
}
