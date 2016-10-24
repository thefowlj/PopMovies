package com.fowlj.popularmovies.service;

import android.app.IntentService;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.fowlj.popularmovies.NetUtil;
import com.fowlj.popularmovies.R;
import com.fowlj.popularmovies.data.MoviesContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by jonfowler on 9/28/16.
 */

public class PopMoviesService extends IntentService {
    private final String LOG_TAG = PopMoviesService.class.getSimpleName();
    private final String BASE_ERROR_MSG = "Error ";

    //API key removed for sharing the code publicly. Replace string w/ MDB API key to make app
    //work. In an ideal production environment the key should be pulled from a server with a
    //secure connection.
    //A unique API key can be obtained for free from https://www.themoviedb.org/faq/account
    //An account may have to be created before requesting an API key
    private String API_KEY;


    final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    final String API_PARAM = "api_key";
    final String APPEND_PARAM = "append_to_response";
    final String MDB_ID_VIDEOS = "videos";
    final String MDB_ID_VIDEO_TYPE = "type";
    final String MDB_ID_VIDEO_KEY = "key";
    final String MDB_ID_VIDEO_TITLE = "name";
    final String MDB_ID_REVIEWS = "reviews";
    final String MDB_ID_REVIEW_AUTHOR = "author";
    final String MDB_ID_REVIEW_TEXT = "content";

    String tableName;
    Uri contentUri;

    public PopMoviesService() {
        super("PopMovies");
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     *               This may be null if the service is being restarted after
     *               its process has gone away; see
     *               {@link Service#onStartCommand}
     *               for details.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        //get the api key passed from the PopularMoviesFragment
        API_KEY = intent.getStringExtra(getString(R.string.api_key_tag));

        String movieJsonStr = null;

        //get the sorting preference passed from the PopularMoviesFragment
        String sortParam = intent.getStringExtra(getString(R.string.pref_sort_by));

        //set the table name and content uri based on the sort preference
        if(sortParam.equalsIgnoreCase("popular")) {
            tableName = MoviesContract.PopularMoviesEntry.TABLE_NAME;
            contentUri = MoviesContract.PopularMoviesEntry.CONTENT_URI;
        } else {
            tableName = MoviesContract.TopRatedMoviesEntry.TABLE_NAME;
            contentUri = MoviesContract.TopRatedMoviesEntry.CONTENT_URI;
        }

        //URI used to pull the JSON data from the MDB
        Uri builtUri;
        builtUri = Uri.parse(BASE_URL + sortParam + "?")
                .buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        //Get the raw data from the MDB server
        try {
            movieJsonStr = NetUtil.getStringFromURL(new URL(builtUri.toString()));
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, BASE_ERROR_MSG, e);
        }

        //Get data from the JSON string
        try {
            getMovieDataFromJson(movieJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, BASE_ERROR_MSG, e);
        } catch(NullPointerException e) {
            Log.e(LOG_TAG, BASE_ERROR_MSG, e);
        }
    }

    /**
     * Get the movie data in a usable format from the provided JSON string.
     * @param movieJsonStr the JSON string returned by MDB
     * @return double String array of results
     * @throws JSONException
     */
    private void getMovieDataFromJson(String movieJsonStr) throws JSONException {

        //if the string received is null then do no proceed
        if(movieJsonStr == null) {
            return;
        }

        //Names of JSON objects to be extracted
        //TODO: this could be done as an array
        final String MDB_LIST = getString(R.string.mdb_id_results_list);
        final String MDB_IMAGE = getString(R.string.mdb_id_poster_path);
        final String MDB_TITLE = getString(R.string.mdb_id_title);
        final String MDB_OVERVIEW = getString(R.string.mdb_id_overview);
        final String MDB_RELEASE_DATE = getString(R.string.mdb_id_release_date);
        final String MDB_VOTE_AVERAGE = getString(R.string.mdb_id_vote_average);
        final String MDB_MOVIE_ID = getString(R.string.mdb_move_id);

        //number of data fields to be saved
        final int NUM_INFO = 6;

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray parentMovieArray = movieJson.getJSONArray(MDB_LIST);

        Vector<ContentValues> cVVector = new Vector<>(parentMovieArray.length());

        //Based on the JSON structure from MDB, the data can be iterated through to retrieve
        //what is needed and placed into the result array to be returned.
        for(int i = 0; i < parentMovieArray.length(); i++) {
            JSONObject movieObject = parentMovieArray.getJSONObject(i);

            String posterPath = movieObject.getString(MDB_IMAGE);
            String title = movieObject.getString(MDB_TITLE);
            String overview = movieObject.getString(MDB_OVERVIEW);
            String releaseDate = movieObject.getString(MDB_RELEASE_DATE);
            String voteAverage = movieObject.getString(MDB_VOTE_AVERAGE);
            String id = movieObject.getString(MDB_MOVIE_ID);


            //The extra data needs to be retrieved from a second JSON query.
            //This is made more efficient by querying for the videos and reviews as well.
            Uri uri = Uri.parse(getString(R.string.mdb_base_url)).buildUpon()
                    .appendPath(id)
                    .appendQueryParameter(getString(R.string.mdb_api_key_param), API_KEY)
                    .appendQueryParameter(APPEND_PARAM, MDB_ID_VIDEOS + "," + MDB_ID_REVIEWS)
                    .build();
            String extraJsonStr = null;
            try {
                 extraJsonStr = NetUtil.getStringFromURL(new URL(uri.toString()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            ContentValues movieValues = new ContentValues();

            JSONObject jsonObj = new JSONObject(extraJsonStr);
            Integer runtime = jsonObj.getInt(getString(R.string.mdb_id_runtime));

            //Both the videos and reviews could be set up as independent tables in the database
            //and joined when using the data, but it is currently implemented using a single table
            //for each sorting preference.
            JSONArray videos = jsonObj.getJSONObject(MDB_ID_VIDEOS).getJSONArray("results");
            JSONObject videoObj;
            int nVideos = videos.length();
            if(nVideos > 0) {
                videoObj = videos.getJSONObject(0);
                movieValues.put(
                        MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE1,
                        videoObj.getString(MDB_ID_VIDEO_TITLE));
                movieValues.put(
                        MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY1,
                        videoObj.getString(MDB_ID_VIDEO_KEY));
                if (nVideos > 1) {
                    videoObj = videos.getJSONObject(1);
                    movieValues.put(
                            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE2,
                            videoObj.getString(MDB_ID_VIDEO_TITLE));
                    movieValues.put(
                            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY2,
                            videoObj.getString(MDB_ID_VIDEO_KEY));

                    if (nVideos > 2) {
                        videoObj = videos.getJSONObject(2);
                        movieValues.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE3,
                                videoObj.getString(MDB_ID_VIDEO_TITLE));
                        movieValues.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY3,
                                videoObj.getString(MDB_ID_VIDEO_KEY));
                    }
                }
            }

            JSONArray reviews = jsonObj.getJSONObject(MDB_ID_REVIEWS).getJSONArray("results");
            JSONObject reviewObj;
            int nReviews = reviews.length();
            if(nReviews > 0) {
                reviewObj = reviews.getJSONObject(0);
                movieValues.put(
                        MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE1,
                        reviewObj.getString(MDB_ID_REVIEW_AUTHOR) + " says: ");
                movieValues.put(
                        MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT1,
                        reviewObj.getString(MDB_ID_REVIEW_TEXT));

                if (nReviews > 1) {
                    reviewObj = reviews.getJSONObject(1);
                    movieValues.put(
                            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE2,
                            reviewObj.getString(MDB_ID_REVIEW_AUTHOR) + " says: ");
                    movieValues.put(
                            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT2,
                            reviewObj.getString(MDB_ID_REVIEW_TEXT));

                    if (nReviews > 2) {
                        reviewObj = reviews.getJSONObject(2);
                        movieValues.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE3,
                                reviewObj.getString(MDB_ID_REVIEW_AUTHOR) + " says: ");
                        movieValues.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT3,
                                reviewObj.getString(MDB_ID_REVIEW_TEXT));
                    }
                }
            }

            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH, posterPath);
            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_TITLE, title);
            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE, releaseDate);
            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_VOTE_AVG, voteAverage);
            movieValues.put(MoviesContract.PopularMoviesEntry.COLUMN_RUNTIME, runtime);

            cVVector.add(movieValues);

        }

        //add to database
        if(cVVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            String where = "_id != -1";
            this.getContentResolver().delete(contentUri, where, null);
            this.getContentResolver().bulkInsert(contentUri, cvArray);
        }
    }
}
