package com.josemgu91.popularmovies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
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

        final List<Movie> favoriteMoviesList = new ArrayList<>();
        for (final String remoteId : favoriteMoviesRemoteIdList) {
            boolean found = false;
            for (final Movie movie : popularMoviesList) {
                if (movie.getId().equals(remoteId)) {
                    favoriteMoviesList.add(movie);
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (final Movie movie : topRatedMoviesList) {
                    if (movie.getId().equals(remoteId)) {
                        favoriteMoviesList.add(movie);
                        break;
                    }
                }
            } else {
                final String movieJson = NetworkUtils.getDetails(remoteId);
                final Movie movie = ServerResponseParser.parseMovie(movieJson);
                favoriteMoviesList.add(movie);
            }
        }

        final List<Movie> moviesToInsertList = new ArrayList<>();
        moviesToInsertList.addAll(topRatedMoviesList);
        for (final Movie movie : popularMoviesList) {
            if (!moviesToInsertList.contains(movie)) {
                moviesToInsertList.add(movie);
            }
        }
        for (final Movie movie : favoriteMoviesList) {
            if (!moviesToInsertList.contains(movie)) {
                moviesToInsertList.add(movie);
            }
        }

        contentResolver.delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        for (final Movie movie : moviesToInsertList) {
            final boolean isFavorite = favoriteMoviesList.contains(movie);
            final boolean isTopRated = topRatedMoviesList.contains(movie);
            final boolean isPopular = popularMoviesList.contains(movie);
            final ContentValues movieContentValues = new ContentValues();
            movieContentValues.put(MovieContract.MovieEntry.IS_FAVORITE, isFavorite);
            movieContentValues.put(MovieContract.MovieEntry.IS_TOP_RATED, isTopRated);
            movieContentValues.put(MovieContract.MovieEntry.IS_MOST_POPULAR, isPopular);
            movieContentValues.put(MovieContract.MovieEntry.PLOT_SYNOPSIS, movie.getPlotSynopsis());
            movieContentValues.put(MovieContract.MovieEntry.POPULARITY, movie.getPopularity());
            movieContentValues.put(MovieContract.MovieEntry.POSTER_REMOTE_ID, movie.getPosterPath());
            movieContentValues.put(MovieContract.MovieEntry.RELEASE_DATE, movie.getReleaseDate());
            movieContentValues.put(MovieContract.MovieEntry.TITLE, movie.getOriginalTitle());
            movieContentValues.put(MovieContract.MovieEntry.REMOTE_ID, movie.getId());
            movieContentValues.put(MovieContract.MovieEntry.USER_RATING, movie.getUserRating());
            final long movieLocalId = ContentUris.parseId(
                    contentResolver.insert(MovieContract.MovieEntry.CONTENT_URI,
                            movieContentValues)
            );
            final List<Review> reviews = reviewsMap.get(movie.getId());
            if (reviews != null) {
                for (final Review review : reviews) {
                    final ContentValues reviewContentValues = new ContentValues();
                    reviewContentValues.put(MovieContract.ReviewEntry.AUTHOR, review.getAuthor());
                    reviewContentValues.put(MovieContract.ReviewEntry.CONTENT, review.getContent());
                    reviewContentValues.put(MovieContract.ReviewEntry.MOVIE_ID, movieLocalId);
                    contentResolver.insert(MovieContract.ReviewEntry.CONTENT_URI,
                            reviewContentValues);
                }
            }
            final List<Video> videos = videosMap.get(movie.getId());
            if (videos != null) {
                for (final Video video : videos) {
                    final ContentValues videoContentValues = new ContentValues();
                    videoContentValues.put(MovieContract.VideoEntry.MOVIE_ID, movieLocalId);
                    videoContentValues.put(MovieContract.VideoEntry.TITLE, video.getName());
                    videoContentValues.put(MovieContract.VideoEntry.URL, video.getUrl().toString());
                    contentResolver.insert(MovieContract.VideoEntry.CONTENT_URI,
                            videoContentValues);
                }
            }
        }

    }

}
