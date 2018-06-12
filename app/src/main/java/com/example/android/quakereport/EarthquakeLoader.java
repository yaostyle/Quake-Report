package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;

    }

    @Override
    protected void onStartLoading() {
        Log.e(LOG_TAG, "onStartLoading called..");
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        Log.e(LOG_TAG, "loadInBackground called..");
        if (mUrl == null) {
            return null;
        }

        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;

    }
}
