package com.example.zsor0001.cinelog;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.zsor0001.cinelog.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String MOVIE_SHARE_HASHTAG = " #CineLogApp";

    private ShareActionProvider mShareActionProvider;
    private String mMovieStr;

    private static final int DETAIL_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_RATING,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_DATE
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_OVERVIEW = 2;
    static final int COL_MOVIE_POPULARITY = 3;
    static final int COL_MOVIE_RELEASE = 4;
    static final int COL_MOVIE_RATING = 5;
    static final int COL_MOVIE_POSTER = 6;
    static final int COL_MOVIE_DATE = 7;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_detail, container, false);

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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        String title = data.getString(COL_MOVIE_TITLE);
        String overview = data.getString(COL_MOVIE_OVERVIEW);
        String rating = data.getString(COL_MOVIE_RATING);
        String release = data.getString(COL_MOVIE_RELEASE);

        mMovieStr = data.getString(COL_MOVIE_POSTER);

        ImageView detailView = (ImageView) getView().findViewById(R.id.detail_text);
        Picasso.with(getContext()).load(mMovieStr).into(detailView);

        TextView titleView = (TextView) getView().findViewById(R.id.title);
        titleView.setText(title);

        TextView releaseView = (TextView) getView().findViewById(R.id.movie_year);
        releaseView.setText(release);

        TextView ratingView = (TextView) getView().findViewById(R.id.movie_rating);
        ratingView.setText(rating);

        TextView overviewView = (TextView) getView().findViewById(R.id.movie_description);
        overviewView.setText(overview);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
