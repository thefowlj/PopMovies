package com.fowlj.popularmovies;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.fowlj.popularmovies.data.MoviesContract;
import com.fowlj.popularmovies.service.PopMoviesService;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * The main Fragment that is displayed in the MainActivity. This Fragment displays a grid of movie
 * posters by either popularity or top rated. The data is pulled from the Movie Database
 * (https://www.themoviedb.org).
 */
public class PopularMoviesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = PopularMoviesFragment.class.getSimpleName();

    //Columns for the movies.db tables
    private static final String[] POPULAR_MOVIES_COLUMNS = {
            MoviesContract.PopularMoviesEntry.TABLE_NAME + "." + MoviesContract.PopularMoviesEntry._ID,
            MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.PopularMoviesEntry.COLUMN_TITLE,
            MoviesContract.PopularMoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.PopularMoviesEntry.COLUMN_VOTE_AVG,
            MoviesContract.PopularMoviesEntry.COLUMN_RUNTIME,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE1,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY1,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE2,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY2,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE3,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY3,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE1,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT1,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE2,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT2,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE3,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT3,
    };

    private static final String[] TOPRATED_MOVIES_COLUMNS = {
            MoviesContract.TopRatedMoviesEntry.TABLE_NAME + "." + MoviesContract.TopRatedMoviesEntry._ID,
            MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.PopularMoviesEntry.COLUMN_TITLE,
            MoviesContract.PopularMoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.PopularMoviesEntry.COLUMN_VOTE_AVG,
            MoviesContract.PopularMoviesEntry.COLUMN_RUNTIME,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE1,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY1,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE2,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY2,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE3,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY3,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE1,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT1,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE2,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT2,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE3,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT3,
    };

    private static final String[] FAVORITE_MOVIES_COLUMNS = {
            MoviesContract.FavoriteMoviesEntry.TABLE_NAME + "." + MoviesContract.FavoriteMoviesEntry._ID,
            MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID,
            MoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH,
            MoviesContract.PopularMoviesEntry.COLUMN_TITLE,
            MoviesContract.PopularMoviesEntry.COLUMN_OVERVIEW,
            MoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE,
            MoviesContract.PopularMoviesEntry.COLUMN_VOTE_AVG,
            MoviesContract.PopularMoviesEntry.COLUMN_RUNTIME,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE1,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY1,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE2,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY2,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE3,
            MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY3,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE1,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT1,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE2,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT2,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE3,
            MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT3,
    };

    //Column indices for the tables
    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_POSTER_PATH = 2;
    static final int COL_TITLE = 3;
    static final int COL_OVERVIEW = 4;
    static final int COL_RELEASE_DATE = 5;
    static final int COL_VOTE_AVG = 6;
    static final int COL_RUNTIME = 7;
    static final int COL_TRAILER_TITLE1 = 8;
    static final int COL_TRAILER_KEY1 = 9;
    static final int COL_TRAILER_TITLE2 = 10;
    static final int COL_TRAILER_KEY2 = 11;
    static final int COL_TRAILER_TITLE3 = 12;
    static final int COL_TRAILER_KEY3 = 13;
    static final int COL_REVIEW_TITLE1 = 14;
    static final int COL_REVIEW_TEXT1 = 15;
    static final int COL_REVIEW_TITLE2 = 16;
    static final int COL_REVIEW_TEXT2 = 17;
    static final int COL_REVIEW_TITLE3 = 18;
    static final int COL_REVIEW_TEXT3 = 19;

    private static final String SAVED_ITEM_SELECTED = "ITEM_POSITION";

    private boolean mTwoPane;
    int selectionPosition = 0;

    //custom adapter used to populate the GridView with images
    ImageAdapter imageAdapter;

    //stores an ArrayList for each movie loaded to from the MDB
    ArrayList<ArrayList<String>> movieInfoArrays = new ArrayList<>();

    //the container that all the movie posters will be populated in
    GridView gridView;

    //base error message
    String BASE_ERROR_MSG = "Error ";

    //List to house our wonderful Movie objects from the database
    ArrayList<Movie> movies = new ArrayList<>();

    private final static String SORT_POPULAR = "popular";
    private final static String SORT_TOP_RATED = "top_rated";
    private final static String SORT_FAVORITES = "favorites";

    public PopularMoviesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //attempt to retrieve the saved selection position of grid view
        if(savedInstanceState != null) {
            selectionPosition = savedInstanceState.getInt(SAVED_ITEM_SELECTED);
        }

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //create a new ImageAdapter if one does not yet exist
        if(imageAdapter == null) {
            imageAdapter = new ImageAdapter(getActivity(), new ArrayList<Uri>());
        }
        gridView = (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(imageAdapter);
        gridView.setDrawSelectorOnTop(true);
        gridView.setSelector(R.drawable.selector);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            gridView.setNestedScrollingEnabled(true);
        }

        updateMovies();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //determine if we are using a two pane layout or not
        mTwoPane = getActivity().findViewById(R.id.movie_detail_container) != null;

        //determine what Movie object to send when a poster image is clicked
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectionPosition = i;

                //if there are no movies loaded, don't do any extra work
                if(movies == null ) {
                    return;
                } else if (movies.size() == 0) {
                    return;
                }
                Movie movie = movies.get(i);
                DetailActivityFragment oldDF = (DetailActivityFragment)getActivity()
                        .getSupportFragmentManager()
                        .findFragmentByTag(MainActivity.DETAILFRAGMENT_TAG);
                if(oldDF != null) {
                    if(oldDF.movie.id.equalsIgnoreCase(movie.id)) { return; }
                }
                if(mTwoPane) {
                    //on a two pane layout, set up the detail fragment to take the Movie info
                    DetailActivityFragment df = new DetailActivityFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.parcelable_movies_tag), movie);
                    df.setArguments(args);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.movie_detail_container,
                                    df,
                                    MainActivity.DETAILFRAGMENT_TAG)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .disallowAddToBackStack()
                            .commit();
                } else {
                    //if a single pane layout, send data through an Intent to start the detail activity
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                    detailIntent.putExtra(getString(R.string.parcelable_movies_tag), movie);
                    startActivity(detailIntent);
                }
            }
        });

        //start the loader
        getLoaderManager().initLoader(0, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void updateMovies() {

        //check to see if there is internet before proceeding
        if(!(NetUtil.isOnline())){
            Toast.makeText(getActivity(), getString(R.string.no_internet_message), Toast.LENGTH_SHORT).show();
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = prefs.getString(
                    getString(R.string.pref_sort_by), getString(R.string.pref_sort_by_default));
            if(sortBy.equalsIgnoreCase("favorites")) {
                return;
            }
            Intent intent = new Intent(getActivity(), PopMoviesService.class);
            intent.putExtra(getString(R.string.api_key_tag), getString(R.string.mdb_api_key));
            intent.putExtra(getString(R.string.pref_sort_by), sortBy);
            getActivity().startService(intent);

            getLoaderManager().restartLoader(0, null, this);
        }

    }

    public void onResume() {
        super.onResume();

        //restart loader
        getLoaderManager().restartLoader(0, null, this);
        //Log.d(LOG_TAG, "selection position: " + selectionPosition);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //determine what URI and columns to use based on the sort preference
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(
                getString(R.string.pref_sort_by), getString(R.string.pref_sort_by_default));
        Uri contentUri = null;
        String[] columns = null;
        switch(sortBy) {
            case SORT_POPULAR:
                contentUri = MoviesContract.PopularMoviesEntry.CONTENT_URI;
                columns = POPULAR_MOVIES_COLUMNS;
                break;
            case SORT_TOP_RATED:
                contentUri = MoviesContract.TopRatedMoviesEntry.CONTENT_URI;
                columns = TOPRATED_MOVIES_COLUMNS;
                break;
            case SORT_FAVORITES:
                contentUri = MoviesContract.FavoriteMoviesEntry.CONTENT_URI;
                columns = FAVORITE_MOVIES_COLUMNS;
                break;
            default:
                Log.e(LOG_TAG, "sortBy error!");
        }
        //Log.d(LOG_TAG, "sortby: " + sortBy);

        return new CursorLoader(getActivity(),
                contentUri,
                columns,
                null,
                null,
                null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.  See {@link android.app.FragmentManager#beginTransaction()
     * FragmentManager.openTransaction()} for further discussion on this.
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.  In particular:
     * <p>
     * <ul>
     * <li> <p>The Loader will monitor for changes to the data, and report
     * them to you through new calls here.  You should not monitor the
     * data yourself.  For example, if the data is a {@link Cursor}
     * and you place it in a {@link CursorAdapter}, use
     * the {@link CursorAdapter#CursorAdapter(Context,
     * Cursor, int)} constructor <em>without</em> passing
     * in either {@link CursorAdapter#FLAG_AUTO_REQUERY}
     * or {@link CursorAdapter#FLAG_REGISTER_CONTENT_OBSERVER}
     * (that is, use 0 for the flags argument).  This prevents the CursorAdapter
     * from doing its own observing of the Cursor, which is not needed since
     * when a change happens you will get a new Cursor throw another call
     * here.
     * <li> The Loader will release the data once it knows the application
     * is no longer using it.  For example, if the data is
     * a {@link Cursor} from a {@link CursorLoader},
     * you should not call close() on it yourself.  If the Cursor is being placed in a
     * {@link CursorAdapter}, you should use the
     * {@link CursorAdapter#swapCursor(Cursor)}
     * method so that the old Cursor is not closed.
     * </ul>
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data != null) {
            //if data exists, populate the Movie array with data
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            HashSet<String> favoritesStringSet =
                    (HashSet<String>)prefs.getStringSet(
                            getString(R.string.pref_favorites_string_set), new HashSet<String>());
            if(imageAdapter != null) imageAdapter.clear();
            if(movies.isEmpty() == false) movies.clear();
            while(data.moveToNext()) {
                String posterPath = data.getString(COL_POSTER_PATH);
                Uri posterUri = getImageUriFromPath(posterPath);
                imageAdapter.add(posterUri);

                Movie movie = new Movie();
                movie.id = data.getString(COL_MOVIE_ID);
                movie.posterPath = posterPath;
                movie.title = data.getString(COL_TITLE);
                movie.overview = data.getString(COL_OVERVIEW);
                movie.releaseDate = data.getString(COL_RELEASE_DATE);
                movie.voteAvg = data.getFloat(COL_VOTE_AVG);
                movie.runtime = data.getInt(COL_RUNTIME);
                movie.favorite = favoritesStringSet.contains(data.getString(COL_MOVIE_ID));

                Video video = new Video();
                String trailerTitle = data.getString(COL_TRAILER_TITLE1);
                if(trailerTitle != null) {
                    video.title = trailerTitle;
                    video.key = data.getString(COL_TRAILER_KEY1);
                    movie.videos.add(video);

                    trailerTitle = data.getString(COL_TRAILER_TITLE2);
                    if(trailerTitle != null) {
                        video = new Video();
                        video.title = trailerTitle;
                        video.key = data.getString(COL_TRAILER_KEY2);
                        movie.videos.add(video);

                        trailerTitle = data.getString(COL_TRAILER_TITLE3);
                        if(trailerTitle != null) {
                            video = new Video();
                            video.title = trailerTitle;
                            video.key = data.getString(COL_TRAILER_KEY3);
                            movie.videos.add(video);
                        }
                    }
                }

                Review review = new Review();
                String reviewTitle = data.getString(COL_REVIEW_TITLE1);
                if(reviewTitle != null) {
                    review.title = reviewTitle;
                    review.text = data.getString(COL_REVIEW_TEXT1);
                    movie.reviews.add(review);

                    reviewTitle = data.getString(COL_REVIEW_TITLE2);
                    if(reviewTitle != null) {
                        review = new Review();
                        review.title = reviewTitle;
                        review.text = data.getString(COL_REVIEW_TEXT2);
                        movie.reviews.add(review);

                        reviewTitle = data.getString(COL_REVIEW_TITLE3);
                        if(reviewTitle != null) {
                            review = new Review();
                            review.title = reviewTitle;
                            review.text = data.getString(COL_REVIEW_TEXT3);
                            movie.reviews.add(review);
                        }
                    }
                }
                movies.add(movie);
            }
        }

        //smooth scroll to the selected position that may have been saved on a instance retrieval
        gridView.post(new Runnable() {
            @Override
            public void run() {
                gridView.smoothScrollToPosition(selectionPosition);
                if(mTwoPane) {
                    gridView.performItemClick(
                            null,
                            selectionPosition,
                            gridView.getAdapter().getItemId(selectionPosition));
                }
            }
        });
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /**
      * Gets a URI for the poster image given the path provided from the MDB JSON data
      * @param imagePath poster image path give by MDB
      * @return complete image URI
      */
    private Uri getImageUriFromPath(String imagePath) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String BASE_URL = getString(R.string.mdb_base_image_url);
        final String IMAGE_SIZE = pref.getString(
                getString(R.string.pref_image_size),
                getString(R.string.mdb_image_size_default));

        //The image path is given from MDB with a '\' at the beginning, which resolves as '%2F'
        //due to percent-encoding. To avoid adding this unwanted encoding into the path, the
        //path is shortened without the '\' character.
        imagePath = imagePath.substring(1);

        Uri uri = Uri.parse(BASE_URL)
                .buildUpon()
                .appendPath(IMAGE_SIZE)
                .appendPath(imagePath)
                .build();

        return uri;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "selection position: " + selectionPosition);
        outState.putInt(SAVED_ITEM_SELECTED, selectionPosition);
        super.onSaveInstanceState(outState);
    }

}
