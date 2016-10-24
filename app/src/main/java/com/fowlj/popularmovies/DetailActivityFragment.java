package com.fowlj.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fowlj.popularmovies.data.MoviesContract;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;

import okhttp3.OkHttpClient;

/**
 * A {@link Fragment} that shows the details of a movie selected from the
 * {@link PopularMoviesFragment}.
 */
public class DetailActivityFragment extends Fragment {

    final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    Movie movie;

    private ShareActionProvider mShareActionProvider;

    public DetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        //get the Intent that called DetailActivity
        Intent intent = getActivity().getIntent();

        //Either get the Movie object from the Intent or from the arguments Bundle.
        //This is determined by what type of device is being used and whether it is a
        //two pane layout or a single pane (phone vs tablet).
        if(intent != null) {
            movie = intent.getParcelableExtra(getString(R.string.parcelable_movies_tag));
        }
        if(movie == null && getArguments() != null) {
            movie = getArguments().getParcelable(getString(R.string.parcelable_movies_tag));
        }

        //If there is no movie object, there is no work to be done, so let's not waste the
        //device's time.
        if(movie == null) {
            return rootView;
        }

        //get the poster image's path on the MDB server
        //Picasso will determine if it can use cached/saved content or redownload the
        //poster image
        String posterPath = movie.posterPath;
        posterPath = getString(R.string.mdb_base_image_url) +
                getString(R.string.mdb_image_size_default) +
                posterPath;
        Uri posterUri = Uri.parse(posterPath);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.detail_poster_image_view);
        new Picasso.Builder(getActivity())
                .downloader(new OkHttp3Downloader(new OkHttpClient()))
                .build()
                .load(posterUri)
                .placeholder(R.drawable.ic_placeholder_image)
                .into(imageView);

        //get the movie's title
        String movieTitle = movie.title;
        final TextView titleTextView = (TextView)rootView.findViewById(R.id.detail_title_text_view);
        titleTextView.setText(movieTitle);
        titleTextView.post(new Runnable() {
            @Override
            public void run() {
                /*This code needs to be run after the layout is properly inflated. Otherwise
                the width of the TextView cannot be properly calculated. This could also be done by
                measuring the physical screen width using WindowsManager, but this will automatically
                deal with the dynamics of padding and other options that could be changed.
                 */
                titleTextView.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        getTextSizeFromWidth(titleTextView));
                titleTextView.setSingleLine();
            }
        });

        //get the release date of the movie
        String yearText = movie.releaseDate;
        yearText = (yearText != null) ? yearText.substring(0, 4) : null ;    //this will get just the year and not day or month
        TextView yearTextView = (TextView) rootView.findViewById(R.id.detail_year_text_view);
        yearTextView.setText(yearText);

        //get the runtime of the movie in minutes
        String runtimeText = String.valueOf(movie.runtime) + " min";
        TextView runtimeTextView = (TextView) rootView.findViewById(R.id.detail_runtime_text_view);
        runtimeTextView.setText(runtimeText);


        //get the average vote rating of the movie out of 10
        float voteRating = movie.voteAvg;

        //The vote rating is populated by MDB to the hundreths place. BigDecimal is used to round
        //the value to the tenths place
        BigDecimal bd = new BigDecimal((double)voteRating);
        bd = bd.setScale(1, RoundingMode.HALF_UP);
        TextView voteRatingTextView = (TextView) rootView.findViewById(R.id.detail_rating_avg_text_view);
        voteRatingTextView.setText(bd.toString() + "/10");

        //check if there are any reviews
        if(movie.overview == null) {
            TextView overviewTitleTextView = (TextView) rootView.findViewById(R.id.overview_title_textview);
            overviewTitleTextView.setVisibility(View.GONE);
        } else {
            //get the summary overview of the movie
            String overviewText = movie.overview;
            TextView overviewTextView = (TextView) rootView.findViewById(R.id.detail_overview_text_view);
            overviewTextView.setText(overviewText);
        }

        //check if there are any trailers/videos
        if(movie.videos.size() < 1) {
            TextView trailersTitleTextView = (TextView) rootView.findViewById(R.id.trailers_title_textview);
            trailersTitleTextView.setVisibility(View.GONE);
        } else {

            //get the trailer list
            LinearLayout trailerLayout = (LinearLayout) rootView.findViewById(R.id.trailer_linear_layout);
            for (Video video : movie.videos) {
                final String urlStr = getString(R.string.youtube_base_url) + video.key;
                View view = inflater.inflate(R.layout.trailer_item_layout, trailerLayout, false);
                ImageButton playButton = (ImageButton) view.findViewById(R.id.trailer_play_button);
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlStr));
                        getContext().startActivity(intent);
                    }
                });

                TextView trailerTitleTextView = (TextView) view.findViewById(R.id.trailer_title_textview);
                trailerTitleTextView.setText(video.title);

                trailerLayout.addView(view);
            }
        }

        //check if there are any reviews
        if(movie.reviews.size() < 1) {
            TextView reviewsTitleTextView = (TextView) rootView.findViewById(R.id.reviews_title_textview);
            reviewsTitleTextView.setVisibility(View.GONE);
        } else {
            //get the reviews
            Markdown4jProcessor markdownProcessor = new Markdown4jProcessor();
            LinearLayout reviewLayout = (LinearLayout) rootView.findViewById(R.id.review_linear_layout);
            for (Review review : movie.reviews) {
                View view = inflater.inflate(android.R.layout.two_line_list_item, reviewLayout, false);

                TextView reviewTitleTextView = (TextView) view.findViewById(android.R.id.text1);
                reviewTitleTextView.setText(review.title);

                TextView reviewContentTextView = (TextView) view.findViewById(android.R.id.text2);
                try {
                    reviewContentTextView.setText(Html.fromHtml(markdownProcessor.process(review.text)));
                } catch (IOException e) {
                    reviewContentTextView.setText(review.text);
                }

                reviewLayout.addView(view);
            }
        }

        //Favorite Button
        final Button favoriteButton = (Button) rootView.findViewById(R.id.favorite_button);
        if(movie.favorite) {
            favoriteButton.setBackgroundColor(getResources().getColor(R.color.colorFavoriteSelected));
        }
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor editor = prefs.edit();

            //A set of strings of movie IDs that have been favorited. This can be used to check
            //across tables if movies are favorited or not.
            HashSet<String> favoritesStringSet =
                    (HashSet<String>)prefs.getStringSet(
                            getString(R.string.pref_favorites_string_set), new HashSet<String>());

            @Override
            public void onClick(View v) {
                //we don't want multiple instances of this code running, so make the button
                //unclickable while the click code is running
                favoriteButton.setClickable(false);
                ContentValues cv = new ContentValues();
                if(movie.favorite) {
                    //if the button is clicked and the movie is already a favorite, remove it from
                    //favorites list and table
                    favoritesStringSet.remove(movie.id);
                    String delWhere = MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID + " = ?";
                    getContext().getContentResolver().delete(
                            MoviesContract.FavoriteMoviesEntry.CONTENT_URI,
                            delWhere,
                            new String[] {movie.id});
                } else {
                    //if the button is clicked and the movie is not yet a favorite, add it to the
                    //favorites list and the favorites table
                    //This code could potentially be added into a method to make it neater.
                    favoritesStringSet.add(movie.id);

                    //add the basic movie data to the table
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_MOVIE_ID, movie.id);
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_POSTER_PATH, movie.posterPath);
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_TITLE, movie.title);
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_OVERVIEW, movie.overview);
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_RELEASE_DATE, movie.releaseDate);
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_VOTE_AVG, movie.voteAvg);
                    cv.put(MoviesContract.PopularMoviesEntry.COLUMN_RUNTIME, movie.runtime);

                    //add the trailers to the table
                    int nVideos = movie.videos.size();
                    Video videoObj;
                    if (nVideos > 0) {
                        videoObj = movie.videos.get(0);
                        cv.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE1,
                                videoObj.title);
                        cv.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY1,
                                videoObj.key);

                        if (nVideos > 1) {
                            videoObj = movie.videos.get(1);
                            cv.put(
                                    MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE2,
                                    videoObj.title);
                            cv.put(
                                    MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY2,
                                    videoObj.key);

                            if (nVideos > 2) {
                                videoObj = movie.videos.get(2);
                                cv.put(
                                        MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_TITLE3,
                                        videoObj.title);
                                cv.put(
                                        MoviesContract.PopularMoviesEntry.COLUMN_TRAILER_KEY3,
                                        videoObj.key);
                            }
                        }
                    }

                    //add the reviews to the table
                    Review reviewObj;
                    int nReviews = movie.reviews.size();
                    if (nReviews > 0) {
                        reviewObj = movie.reviews.get(0);
                        cv.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE1,
                                reviewObj.title);
                        cv.put(
                                MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT1,
                                reviewObj.text);

                        if (nReviews > 1) {
                            reviewObj = movie.reviews.get(1);
                            cv.put(
                                    MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE2,
                                    reviewObj.title);
                            cv.put(
                                    MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT2,
                                    reviewObj.text);

                            if (nReviews > 2) {
                                reviewObj = movie.reviews.get(2);
                                cv.put(
                                        MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TITLE3,
                                        reviewObj.title);
                                cv.put(
                                        MoviesContract.PopularMoviesEntry.COLUMN_REVIEW_TEXT3,
                                        reviewObj.text);
                            }
                        }
                    }
                }

                //if there's data to add to the table, add it here
                if(cv.size() > 0) {
                    getContext().getContentResolver().insert(
                            MoviesContract.FavoriteMoviesEntry.CONTENT_URI, cv);
                }

                //save the favorite movies IDs string set to SharedPreferences
                editor.putStringSet(
                        getString(R.string.pref_favorites_string_set), favoritesStringSet);
                editor.apply();

                //flip the boolean of whether the movie is a favorite
                movie.favorite = !movie.favorite;

                //change the state of the Favorite button
                if(movie.favorite) {
                    favoriteButton.setBackgroundColor(getResources().getColor(R.color.colorFavoriteSelected));
                } else {
                    favoriteButton.setBackgroundResource(android.R.drawable.btn_default);
                }

                //make the button clickable again
                favoriteButton.setClickable(true);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //Inflate the menu; this adds items to the action bar if it is present
        inflater.inflate(R.menu.menu_detail_fragment, menu);

        //Retrieve the share menu item
        MenuItem shareMenuItem = menu.findItem(R.id.action_share);

        //Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareMenuItem);

        //if the movie object exists create the share intent
        if(movie != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Create the share Intent
     */
    private Intent createShareMovieIntent() {
        String shareStr;
        if(movie.videos.size() > 0) {
            Video video = movie.videos.get(0);
            String videoUrlStr = getString(R.string.youtube_base_url) + video.key;
            shareStr = movie.title + " - " + video.title + ": " + videoUrlStr;
        } else {
            shareStr = movie.title + " looks awesome!";
        }

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareStr);
        return shareIntent;

    }

    /**
     * This method is adapted from a text scaling method I developed for animated progress objects
     * in another project. The TextView in this case already has a size that will be used as the
     * max text size. This method will reduce that text size if it makes the text longer than a
     * single line on the screen.
     * @param textView {@link TextView} that the text size will be checked
     * @return the preferred text size
     */
    private float getTextSizeFromWidth(TextView textView) {
        float textSize = textView.getTextSize();
        float maxWidth = textView.getWidth();   //this assumes the textView is set to match_parent
        String text = textView.getText().toString();
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);

        //saves the dimensions of the text at the set size to the bounds Rect
        paint.getTextBounds(text, 0, text.length(), bounds);

        //check if the text is already within the width limits
        if(bounds.width() < maxWidth) {
            return textSize;
        } else {
            //if the text is not within the width limits, reduce the size of the text until it is
            while(bounds.width() > maxWidth) {
                textSize--;
                paint.setTextSize(textSize);
                paint.getTextBounds(text, 0, text.length(), bounds);
            }
        }

        //the text size is reduced by 5 to give some padding
        return textSize - 5;
    }
}
