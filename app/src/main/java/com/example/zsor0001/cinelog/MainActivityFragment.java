package com.example.zsor0001.cinelog;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.zsor0001.cinelog.data.MovieContract;
import com.example.zsor0001.cinelog.sync.MovieSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_OVERVIEW = 2;
    static final int COL_MOVIE_POPULARITY = 3;
    static final int COL_MOVIE_RELEASE = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_POSTER = 6;

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

        mAdapter = new ImageAdapter(getActivity(), null, 0, MOVIES_LOADER);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(mAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    final int MOVIE_ID_COL = MainActivityFragment.COL_MOVIE_ID;
                    Uri movieUri = MovieContract.MovieEntry.buildMovieUri(cursor.getLong(MOVIE_ID_COL));
                    intent.setData(movieUri);
                    startActivity(intent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private void updateMovies() {

        MovieSyncAdapter.syncImmediately(getActivity());

        /*
        Intent alarmIntent = new Intent(getActivity(), MovieService.AlarmReceiver.class);

        // Wrap in a pending intent which only fires once
        PendingIntent pi = PendingIntent.getBroadcast(getActivity(), 0, alarmIntent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager am = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);

        // Set the AlarmManager to wake up the system
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pi);

        /*
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute();
        */
    }
/*
    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }
    */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";

        return new CursorLoader(getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                new String[] { MovieContract.MovieEntry._ID, MovieContract.MovieEntry.COLUMN_POSTER_PATH },
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

}
