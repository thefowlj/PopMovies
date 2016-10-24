package com.fowlj.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Defines the content provider for the movies
 */

public class MoviesProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static final int MOVIES_POPULAR = 100;
    static final int MOVIES_TOPRATED = 101;
    static final int MOVIES_FAVORITES = 102;

    private static final SQLiteQueryBuilder sMoviesPopularQueryBuilder;

    static {
        sMoviesPopularQueryBuilder = new SQLiteQueryBuilder();

        //When joins need to happen, they can be put here
    }


    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MoviesContract.PATH_MOVIES_POPULAR, MOVIES_POPULAR);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES_TOPRATED, MOVIES_TOPRATED);
        matcher.addURI(authority, MoviesContract.PATH_MOVIES_FAVORITES, MOVIES_FAVORITES);

        return matcher;
    }

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     * <p>
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     * <p>
     * <p>If you use SQLite, {@link SQLiteOpenHelper}
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * {@link SQLiteOpenHelper#getReadableDatabase} or
     * {@link SQLiteOpenHelper#getWritableDatabase}
     * from this method.  (Instead, override
     * {@link SQLiteOpenHelper#onOpen} to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @NonNull
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch(match) {
            case MOVIES_POPULAR:
                return MoviesContract.PopularMoviesEntry.CONTENT_TYPE;
            case MOVIES_TOPRATED:
                return MoviesContract.TopRatedMoviesEntry.CONTENT_TYPE;
            case MOVIES_FAVORITES:
                return MoviesContract.FavoriteMoviesEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    /**
     * Implement this to handle query requests from clients.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     *
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     *
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     *
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        //Currently just testing out one type of URI, so we will just return a cursor with a
        //database query
        Cursor retCursor;
        String tableName;
        int match = sUriMatcher.match(uri);

        switch(match){
            case MOVIES_POPULAR:
                tableName = MoviesContract.PopularMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_TOPRATED:
                tableName = MoviesContract.TopRatedMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_FAVORITES:
                tableName = MoviesContract.FavoriteMoviesEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
        retCursor = mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }



    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link android.content.ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri = null;

        String tableName;
        switch(match){
            case MOVIES_POPULAR:
                tableName = MoviesContract.PopularMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_TOPRATED:
                tableName = MoviesContract.TopRatedMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_FAVORITES:
                tableName = MoviesContract.FavoriteMoviesEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        //We are only managing one Uri type for the moment
        long _id = db.insert(tableName, null, values);
        if(_id > 0) {
            retUri = null;
        } else {
            throw new SQLException("Failed to insert row into " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link android.content.ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after deleting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs
     * @return The number of rows affected.
     * @throws android.database.sqlite.SQLiteException
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if(null == selection) selection = "1";

        String tableName;
        switch(match){
            case MOVIES_POPULAR:
                tableName = MoviesContract.PopularMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_TOPRATED:
                tableName = MoviesContract.TopRatedMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_FAVORITES:
                tableName = MoviesContract.FavoriteMoviesEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        rowsDeleted = db.delete(
                tableName, selection, selectionArgs);

        if(rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call {@link android.content.ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after updating.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        String tableName;
        switch(match){
            case MOVIES_POPULAR:
                tableName = MoviesContract.PopularMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_TOPRATED:
                tableName = MoviesContract.TopRatedMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_FAVORITES:
                tableName = MoviesContract.FavoriteMoviesEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        rowsUpdated = db.update(
                tableName, values, selection, selectionArgs);

        if(rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int retCount = 0;

        String tableName;
        switch(match){
            case MOVIES_POPULAR:
                tableName = MoviesContract.PopularMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_TOPRATED:
                tableName = MoviesContract.TopRatedMoviesEntry.TABLE_NAME;
                break;
            case MOVIES_FAVORITES:
                tableName = MoviesContract.FavoriteMoviesEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        db.beginTransaction();
        try {
            for(ContentValues value : values) {
                long _id =  db.insert(
                        tableName, null, value);
                if(_id != -1) {
                    retCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return retCount;
    }
}
