package com.codinginflow.sedena.Globales;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


public class GlobalesCifrado extends Application {

	public static final String TAG = com.codinginflow.sedena.Globales.GlobalesCifrado.class.getSimpleName();

	private RequestQueue mRequestQueue;

	private static com.codinginflow.sedena.Globales.GlobalesCifrado InstanceCifrado;

	@Override
	public void onCreate() {
		super.onCreate();
		InstanceCifrado = this;
	}

	public static synchronized com.codinginflow.sedena.Globales.GlobalesCifrado getInstance() {
		return InstanceCifrado;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			mRequestQueue = Volley.newRequestQueue(getApplicationContext());
		}

		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
		getRequestQueue().add(req);
	}

	public <T> void addToRequestQueue(Request<T> req) {
		req.setTag(TAG);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		if (mRequestQueue != null) {
			mRequestQueue.cancelAll(tag);
		}
	}
}