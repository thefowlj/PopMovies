package com.fowlj.popularmovies;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.List;

import okhttp3.OkHttpClient;


/**
 * ImageAdapter was adapted from the example code on Udacity's GitHub account:
 * https://github.com/udacity/android-custom-arrayadapter
 *
 * ImageAdapter uses Picasso to import a list of given Uri links.
 */
public class ImageAdapter extends ArrayAdapter<Uri> {
    private static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    private Picasso mBuilder;

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param uriList        List of URLs where the images are stored.
     */
    public ImageAdapter(Activity context, List<Uri> uriList) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, uriList);

        //Make one builder for an instance of ImageAdapter insetad of creating a new one
        //each time getView() is called.
        OkHttpClient ohc = new OkHttpClient();
        mBuilder = new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(ohc))
                .build();

    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    @NonNull
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the Uri object
        Uri uri = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_layout, parent, false);
        } else {
            convertView.forceLayout();
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image_view);

        //load the image using Picasso
        mBuilder.load(uri)
                .placeholder(R.mipmap.ic_placeholder_image)
                .into(imageView);

        return convertView;
    }
}