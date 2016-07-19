package com.example.zsor0001.cinelog;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ImageAdapter mAdapter;
    //public String[] resultStr;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainactivity_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mAdapter = new ImageAdapter(getActivity(), new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){

                String info = mAdapter.getItem(position);
                Toast.makeText(getActivity(), info, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void updateMovies() {
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {


        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getMovieDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            //final String OWM_OVERVIEW = "overview";
            final String OWM_RELEASE = "release_date";
            //final String OWM_ID = "id";
            final String OWM_TITLE = "title";
            //final String OWM_POPULARITY = "popularity";
            final String OWM_PATH = "poster_path";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OWM_LIST);

            String[] resultStr = new String[20];
            for (int i = 0; i < 20; i++) {

                // Get the JSON object representing the day
                JSONObject movieMain = movieArray.getJSONObject(i);

                //JSONObject overviewObject = movieMain.getJSONObject(OWM_OVERVIEW);
                //JSONObject releaseObject = movieMain.getJSONObject(OWM_RELEASE);
                //JSONObject idObject = movieMain.getJSONObject(OWM_ID);
                //JSONObject titleObject = movieMain.getJSONObject(OWM_TITLE);
                //JSONObject popularityObject = movieMain.getJSONObject(OWM_POPULARITY);
                //JSONObject pathObject = movieMain.getJSONObject(OWM_PATH);

                String title = movieMain.getString(OWM_TITLE);
                String release = movieMain.getString(OWM_RELEASE);

                // Construct the URL for the TheMovieDb query
                final String baseUrl =
                        "http://image.tmdb.org/t/p/w185";
                final String posterPath = movieMain.getString(OWM_PATH);

                String posterURL = baseUrl.concat(posterPath);

                resultStr[i] = posterURL;
            }

            for (String s : resultStr) {
                Log.v(LOG_TAG, "Movie poster: " + s);
            }
            return resultStr;
        }

        @Override
        protected String[] doInBackground(Void... params) {


            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            String sort_by = "popularity.desc";
            String appID = "b2afxxxxxxxxxxxxxxxxf1e6458";

            try {
                // Construct the URL for the TheMovieDb query
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, sort_by)
                        .appendQueryParameter(APPID_PARAM, appID)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                // Create the request to TheMovieDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie str: " + forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the string.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            if (result != null) {
                mAdapter.clear();
                for (String movieStr : result) {
                    mAdapter.add(movieStr);
                }
                // New data is back from the server.  Hooray!
            }
        }
    }
}
