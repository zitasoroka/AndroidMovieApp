package com.example.zsor0001.cinelog;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.zsor0001.cinelog.data.MovieContract;

public class FetchMoviesTask extends AsyncTask<Void, Void, String[]> {


    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private boolean DEBUG = true;

    private ArrayAdapter<String> mAdapter;
    private final Context mContext;

    public FetchMoviesTask(Context context, ArrayAdapter<String> movieAdapter) {
        mContext = context;
        mAdapter = movieAdapter;
    }

    /**
     * Take the String representing the complete movie in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getMovieDataFromJson(String forecastJsonStr)
            throws JSONException {

        String[] resultStr = new String[100];

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_TITLE = "title";
        final String OWM_OVERVIEW = "overview";
        final String OWM_RELEASE = "release_date";
        final String OWM_VOTE_AVERAGE = "vote_average";
        final String OWM_PATH = "poster_path";
        final String OWM_POPULARITY = "popularity";

        try {
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray movieArray = forecastJson.getJSONArray(OWM_LIST);

            //ArrayList<String> group = new ArrayList<String>();

            // Insert the new movie information into database
            Vector<ContentValues> cVVector = new Vector<>(movieArray.length());
            for (int i = 0; i < movieArray.length(); i++) {

                // Get the JSON object representing the movie
                JSONObject movieMain = movieArray.getJSONObject(i);

                String title = movieMain.getString(OWM_TITLE);
                String overview = movieMain.getString(OWM_OVERVIEW);
                String release_date = movieMain.getString(OWM_RELEASE);
                double vote_average = movieMain.getDouble(OWM_VOTE_AVERAGE);
                double popularity = movieMain.getDouble(OWM_POPULARITY);

                // Construct the URL for the TheMovieDb query
                final String baseUrl =
                        "http://image.tmdb.org/t/p/w185";
                final String posterPath = movieMain.getString(OWM_PATH);

                String posterURL = baseUrl.concat(posterPath);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RATING, vote_average);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);

                cVVector.add(movieValues);

                resultStr[i] = posterURL;
            }

            int inserted = 0;

            if (cVVector.size() > 0) {

                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            /*

            String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            Uri movieUri = MovieContract.MovieEntry.CONTENT_URI;

            Cursor cur = mContext.getContentResolver().query(movieUri, null, null,
                    null, sortOrder);

            cVVector = new Vector<>(cur.getCount());
            if (cur.moveToFirst()) {
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVVector.add(cv);
                } while (cur.moveToNext());
            }
            */

            Log.d(LOG_TAG, "FetchMoviesTask Complete. " + inserted + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        String moviesJsnStr = null;

        String sort_by = "popularity.desc";
        String appID = "b2af4d3650a777ceaaaa5199cf1e6458";

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
            moviesJsnStr = buffer.toString();

            Log.v(LOG_TAG, "Movie string: " + moviesJsnStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the movie data, there's no point in attemping
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
            return getMovieDataFromJson(moviesJsnStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        // This will only happen if there was an error getting or parsing the data.
        return null;
    }

    @Override
    protected void onPostExecute(String[] result) {

        if (result != null) {
            mAdapter.clear();
            for (String postersStr : result) {
                mAdapter.add(postersStr);
            }
            // New data is back from the server.  Hooray!
        }
    }
}
