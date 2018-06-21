/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private EarthquakeAdapter mAdapter;
    private static final String USGS_REQUEST_URL = "http://earthquake.usgs.gov/fdsnws/event/1/query";
    private static final int EARTHQUAKE_LOADER_ID = 1;
    private TextView mEmptyStateTextView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "onCraeteLoader called..");

        SharedPreferences sharePrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharePrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);

        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "10");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", "time");

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        mEmptyStateTextView.setText(R.string.no_earthquakes);
        mAdapter.clear();
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
        Log.e(LOG_TAG, "onLoadFinished called..");
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        mAdapter.clear();
        Log.e(LOG_TAG, "onLoaderReset called..");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        ListView earthquakeListView = (ListView) findViewById(R.id.list);


        mAdapter = new EarthquakeAdapter(this, new ArrayList<Earthquake>());

        earthquakeListView.setAdapter(mAdapter);

        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Earthquake currentEarthquake = mAdapter.getItem(i);

                Uri earthquakeUri = Uri.parse(currentEarthquake.getUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                startActivity(websiteIntent);
            }
        });


        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        earthquakeListView.setEmptyView(mEmptyStateTextView);

        /* No longer needed
        EarthquakeAsyncTask task = new EarthquakeAsyncTask();
        task.execute(USGS_REQUEST_URL);
        */

        //LoaderManager loaderManager = getLoaderManager();

        //loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        //Log.e(LOG_TAG, "Init the loader..");

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    /* No longer needed
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, List<Earthquake>> {

        @Override
        protected List<Earthquake> doInBackground(String... urls) {
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            List<Earthquake> result = QueryUtils.fetchEarthquakeData(urls[0]);
            return result;
        }

        @Override
        protected void onPostExecute(List<Earthquake> data) {
            mAdapter.clear();

            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
            }
        }
    }
    */
}
