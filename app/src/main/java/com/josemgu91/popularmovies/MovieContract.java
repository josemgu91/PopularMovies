package com.josemgu91.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by jose on 12/3/17.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.josemgu91.popularmovies.ContentProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";

    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movies";

        public static final String TITLE = "title";
        public static final String PLOT_SYNOPSIS = "plotSynopsis";
        public static final String RELEASE_DATE = "releaseDate";
        public static final String POSTER_URL = "posterUrl";
        public static final String USER_RATING = "userRating";
        public static final String POPULARITY = "popularity";
        public static final String IS_FAVORITE = "isFavorite";

    }

    public static class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_REVIEW)
                .build();

        public static final String TABLE_NAME = "reviews";

        public static final String AUTHOR = "author";
        public static final String CONTENT = "content";
        public static final String MOVIE_ID = "movieId";

    }

    public static class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TRAILER)
                .build();

        public static final String TABLE_NAME = "trailers";

        public static final String TITLE = "title";
        public static final String URL = "url";
        public static final String MOVIE_ID = "movieId";

    }

}
