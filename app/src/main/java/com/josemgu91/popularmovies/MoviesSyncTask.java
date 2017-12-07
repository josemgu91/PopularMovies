package com.josemgu91.popularmovies;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;

import com.josemgu91.popularmovies.network.Movie;
import com.josemgu91.popularmovies.network.NetworkUtils;
import com.josemgu91.popularmovies.network.Review;
import com.josemgu91.popularmovies.network.ServerResponseParser;
import com.josemgu91.popularmovies.network.Video;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jose on 12/5/17.
 */

public class MoviesSyncTask {

    synchronized public static void syncMovies(final Context context) throws IOException, JSONException {

        final ContentResolver contentResolver = context.getContentResolver();

        final String popularMoviesJsonString = NetworkUtils.getPopularMovies();
        final String topRatedMoviesJsonString = NetworkUtils.getTopRatedMovies();
        final List<Movie> popularMoviesList = ServerResponseParser.parseMovies(popularMoviesJsonString);
        final List<Movie> topRatedMoviesList = ServerResponseParser.parseMovies(topRatedMoviesJsonString);

        final List<String> favoriteMoviesRemoteIdList = new ArrayList<>();
        final Cursor favoriteMoviesRemoteIdCursor = contentResolver.query(
                MovieContract.MovieEntry.CONTENT_URI,
                new String[]{MovieContract.MovieEntry.REMOTE_ID},
                MovieContract.MovieEntry.IS_FAVORITE + "=?",
                new String[]{"1"},
                null
        );
        if (favoriteMoviesRemoteIdCursor != null) {
            while (favoriteMoviesRemoteIdCursor.moveToNext()) {
                favoriteMoviesRemoteIdList.add(
                        favoriteMoviesRemoteIdCursor.getString(
                                favoriteMoviesRemoteIdCursor.getColumnIndex(MovieContract.MovieEntry.REMOTE_ID)
                        )
                );
            }
            favoriteMoviesRemoteIdCursor.close();
        }

        final List<String> moviesRemoteIdList = new ArrayList<>();
        for (final Movie movie : popularMoviesList) {
            final String remoteId = movie.getId();
            if (!moviesRemoteIdList.contains(remoteId)) {
                moviesRemoteIdList.add(remoteId);
            }
        }
        for (final Movie movie : topRatedMoviesList) {
            final String remoteId = movie.getId();
            if (!moviesRemoteIdList.contains(remoteId)) {
                moviesRemoteIdList.add(remoteId);
            }
        }
        for (final String remoteId : favoriteMoviesRemoteIdList) {
            if (!moviesRemoteIdList.contains(remoteId)) {
                moviesRemoteIdList.add(remoteId);
            }
        }

        final Map<String, List<Video>> videosMap = new HashMap<>();
        final Map<String, List<Review>> reviewsMap = new HashMap<>();
        for (final String remoteId : moviesRemoteIdList) {
            final String videosJsonString = NetworkUtils.getVideos(remoteId);
            final List<Video> videos = ServerResponseParser.parseVideos(videosJsonString);
            final String reviewsJsonString = NetworkUtils.getReviews(remoteId);
            final List<Review> reviews = ServerResponseParser.parseReviews(reviewsJsonString);
            videosMap.put(remoteId, videos);
            reviewsMap.put(remoteId, reviews);
        }
    }

}
