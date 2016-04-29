package com.stalled.bathroam;

import android.app.Application;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/*
 * This class handles creating and queueing the async tasks for web requests
 */

public class RequestHandler extends Application{

	private RequestQueue mRequestQueue;
	private static RequestHandler mInstance;

	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;
	}

	public static synchronized RequestHandler getInstance() {
		if(mInstance == null){
			mInstance = new RequestHandler();
		}
		return mInstance;
	}

	public RequestQueue getReqQueue(Context context) {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(context);
		}

		return mRequestQueue;
	}

	public <T> void addToReqQueue(Request<T> req, String tag, Context context) {

		getReqQueue(context).add(req);
	}

	public <T> void addToReqQueue(Request<T> req, Context context) {

		getReqQueue(context).add(req);
	}

	public void cancelPendingReq(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}

}