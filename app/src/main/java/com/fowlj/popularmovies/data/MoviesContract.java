package com.fowlj.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the columns and URIs for the data
 */

public class MoviesContract {

    public static final String CONTENT_AUTHORITY = "com.fowlj.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES_POPULAR = "movies_popular";
    public static final String PATH_MOVIES_TOPRATED = "movies_toprated";
    public static final String PATH_MOVIES_FAVORITES = "movies_favorites";

    public static final class PopularMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES_POPULAR)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES_POPULAR;

        public static final String TABLE_NAME = "movies_popular";

        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static final String COLUMN_POSTER_PATH = "poster_path";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_VOTE_AVG = "vote_average";

        public static final String COLUMN_RUNTIME = "runtime";

        public static final String COLUMN_TRAILER_TITLE1 = "trailer_title1";
        public static final String COLUMN_TRAILER_KEY1 = "trailer_key1";
        public static final String COLUMN_TRAILER_TITLE2 = "trailer_title2";
        public static final String COLUMN_TRAILER_KEY2 = "trailer_key2";
        public static final String COLUMN_TRAILER_TITLE3 = "trailer_title3";
        public static final String COLUMN_TRAILER_KEY3 = "trailer_key3";

        public static final String COLUMN_REVIEW_TITLE1 = "review_title1";
        public static final String COLUMN_REVIEW_TEXT1 = "review_text1";
        public static final String COLUMN_REVIEW_TITLE2 = "review_title2";
        public static final String COLUMN_REVIEW_TEXT2 = "review_text2";
        public static final String COLUMN_REVIEW_TITLE3 = "review_title3";
        public static final String COLUMN_REVIEW_TEXT3 = "review_text3";
    }

    public static final class TopRatedMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES_TOPRATED)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES_TOPRATED;

        public static final String TABLE_NAME = "movies_toprated";
    }

    public static final class FavoriteMoviesEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIES_FAVORITES)
                .build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" +
                        PATH_MOVIES_FAVORITES;

        public static final String TABLE_NAME = "movies_favorites";
    }
}
