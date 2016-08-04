package com.example.zsor0001.cinelog.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Defines table and column names for the moviw database.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.zsor0001.cinelog";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.zsor0001.cinelog.app/movie is a valid path for
    // looking at movie data
    public static final String PATH_MOVIE = "movies";

    public static long normalizeDate(long startDate) {
        // normalize the start date to the beginning of the (UTC) day
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    /* Inner class that defines the table contents of the popular_movies table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movies";

        // Movie id as returned by API, to identify the icon to be used
        //public static final String COLUMN_MOVIE_ID = "movie_id";

        // Overview of the movie
        public static final String COLUMN_OVERVIEW = "overview";

        // Release date stored as string
        public static final String COLUMN_RELEASE_DATE = "release";

        // Title sored as string
        public static final String COLUMN_TITLE = "title";

        // Rating
        public static final String COLUMN_RATING = "rating";

        // Popularity
        public static final String COLUMN_POPULARITY = "popularity";

        // Path of the poster
        public static final String COLUMN_POSTER_PATH = "poster_path";

        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_DATE = "date";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getId(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static Uri buildMoviesWithDate(long date) {
            return CONTENT_URI.buildUpon().appendPath(Long.toString(normalizeDate(date))).build();

        }
    }
}
