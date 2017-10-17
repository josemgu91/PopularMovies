package com.josemgu91.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 10/17/17.
 */

public class ServerResponseParser {

    private final static String KEY_RESULTS = "results";
    private final static String KEY_MOVIE_ID = "id";
    private final static String KEY_MOVIE_POSTER_PATH = "poster_path";
    private final static String KEY_MOVIE_ORIGINAL_TITLE = "original_title";
    private final static String KEY_MOVIE_PLOT_SYNOPSIS = "overview";
    private final static String KEY_MOVIE_USER_RATING = "vote_average";
    private final static String KEY_MOVIE_RELEASE_DATE = "release_date";

    public static List<Movie> parseMovies(final String jsonString) throws JSONException {
        final ArrayList<Movie> movies = new ArrayList<>();
        final JSONObject jsonResponse = new JSONObject(jsonString);
        final JSONArray moviesJsonArray = jsonResponse.getJSONArray(KEY_RESULTS);
        for (int i = 0; i < moviesJsonArray.length(); i++) {
            final JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);
            final String id = movieJsonObject.getString(KEY_MOVIE_ID);
            final String posterPath = movieJsonObject.getString(KEY_MOVIE_POSTER_PATH);
            final String originalTitle = movieJsonObject.getString(KEY_MOVIE_ORIGINAL_TITLE);
            final String plotSynopsis = movieJsonObject.getString(KEY_MOVIE_PLOT_SYNOPSIS);
            final String userRating = movieJsonObject.getString(KEY_MOVIE_USER_RATING);
            final String releaseDate = movieJsonObject.getString(KEY_MOVIE_RELEASE_DATE);
            final Movie movie = new Movie(id, posterPath, originalTitle, plotSynopsis, userRating, releaseDate);
            movies.add(movie);
        }
        return movies;
    }

}
