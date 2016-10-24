package com.fowlj.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


/**
 * A Parcelable object to pass movie data between fragments/activities. The DetailActivityFragment
 * then uses this data to populate its views.
 */

class Movie implements Parcelable {

    String id;
    String posterPath;
    String title;
    String overview;
    String releaseDate;
    float voteAvg;
    List<Video> videos;
    List<Review> reviews;
    int runtime;
    boolean favorite;

    Movie() {
        videos = new ArrayList<>();
        reviews = new ArrayList<>();
    }

    private Movie(Parcel in) {
        this.id = in.readString();
        this.posterPath = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.voteAvg = in.readFloat();
        if(videos == null) {
            videos = new ArrayList<>();
        }
        in.readTypedList(videos, Video.CREATOR);
        if(reviews == null) {
            reviews = new ArrayList<>();
        }
        in.readTypedList(reviews, Review.CREATOR);
        this.runtime = in.readInt();
        this.favorite = in.readByte() != 0;     //favorite == true if byte != 0
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(posterPath);
        dest.writeString(title);
        dest.writeString(overview);
        dest.writeString(releaseDate);
        dest.writeFloat(voteAvg);
        dest.writeTypedList(videos);
        dest.writeTypedList(reviews);
        dest.writeInt(runtime);
        dest.writeByte((byte) (favorite ? 1 : 0));      //if favorite == true, byte == 1
    }
}
