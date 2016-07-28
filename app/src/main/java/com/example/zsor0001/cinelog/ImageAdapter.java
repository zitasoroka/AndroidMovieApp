package com.example.zsor0001.cinelog;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.zsor0001.cinelog.data.MovieContract;
import com.squareup.picasso.Picasso;

public class ImageAdapter extends CursorAdapter {

    private static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;
    private static int sLoaderID;

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     * @param context The current context. Used to inflate the layout file.
     * @param c A List of String objects to display in a list
     */
    public ImageAdapter(Context context, Cursor c, int flags, int loaderID) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single ImageView.

        super(context, c, flags);
        Log.d(LOG_TAG, "ImageAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    private String convertCursorToPosterPath(Cursor cursor) {

        return cursor.getString(MainActivityFragment.COL_MOVIE_POSTER);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        Log.d(LOG_TAG, "In new View");

        return LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        Log.d(LOG_TAG, "In bind View");

        int moviePoster = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String path = cursor.getString(moviePoster);

        Log.i(LOG_TAG, "Image reference extracted: " + path);

        ImageView iconView = (ImageView) view.findViewById(R.id.picture);
        Picasso.with(context).load(path).into(iconView);
    }
}

    /*

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        String thumbItems = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridview_item, parent, false);
        }

        ImageView iconView = (ImageView) convertView.findViewById(R.id.picture);
        //iconView.setImageResource(thumbItems.drawableId);

        Picasso.with(getContext()).load(thumbItems).into(iconView);

        return convertView;
    }
    */
