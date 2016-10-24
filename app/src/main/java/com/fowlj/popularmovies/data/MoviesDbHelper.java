package com.fowlj.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fowlj.popularmovies.data.MoviesContract.PopularMoviesEntry;

/**
 * Database helper class for the app
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_POPULAR_MOVIES_TABLE = "CREATE TABLE " +
                PopularMoviesEntry.TABLE_NAME + " (" +
                PopularMoviesEntry._ID + " INTEGER PRIMARY KEY," +
                PopularMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                PopularMoviesEntry.COLUMN_RUNTIME + " REAL NOT NULL, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT3 + " TEXT " +
                " );";

        final String SQL_CREATE_TOPRATED_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.TopRatedMoviesEntry.TABLE_NAME + " (" +
                MoviesContract.TopRatedMoviesEntry._ID + " INTEGER PRIMARY KEY," +
                PopularMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                PopularMoviesEntry.COLUMN_RUNTIME + " REAL NOT NULL, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT3 + " TEXT " +
                " );";

        final String SQL_CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " +
                MoviesContract.FavoriteMoviesEntry.TABLE_NAME + " (" +
                MoviesContract.FavoriteMoviesEntry._ID + " INTEGER PRIMARY KEY," +
                PopularMoviesEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                PopularMoviesEntry.COLUMN_VOTE_AVG + " REAL NOT NULL, " +
                PopularMoviesEntry.COLUMN_RUNTIME + " REAL NOT NULL, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_TITLE3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_TRAILER_KEY3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT1 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT2 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TITLE3 + " TEXT, " +
                PopularMoviesEntry.COLUMN_REVIEW_TEXT3 + " TEXT " +
                " );";

        db.execSQL(SQL_CREATE_POPULAR_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_TOPRATED_MOVIES_TABLE);
        db.execSQL(SQL_CREATE_FAVORITE_MOVIES_TABLE);
    }

    /**
     * Called when the database needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     * <p>
     * <p>
     * The SQLite ALTER TABLE documentation can be found
     * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
     * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
     * you can use ALTER TABLE to rename the old table, then create the new table and then
     * populate the new table with the contents of the old table.
     * </p><p>
     * This method executes within a transaction.  If an exception is thrown, all changes
     * will automatically be rolled back.
     * </p>
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropStr = "DROP TABLE IF EXISTS ";
        db.execSQL(dropStr + PopularMoviesEntry.TABLE_NAME);
        db.execSQL(dropStr + MoviesContract.TopRatedMoviesEntry.TABLE_NAME);
        db.execSQL(dropStr + MoviesContract.FavoriteMoviesEntry.TABLE_NAME);
        onCreate(db);
    }


}
