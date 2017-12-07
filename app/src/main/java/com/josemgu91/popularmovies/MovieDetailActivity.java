package com.josemgu91.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.josemgu91.popularmovies.network.Movie;
import com.josemgu91.popularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

/**
 * Created by jose on 10/13/17.
 */

public class MovieDetailActivity extends AppCompatActivity {

    public final static String PARAM_MOVIE = "com.josemgu91.popularmovies.MOVIE";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        final ImageView imageViewMoviePoster = findViewById(R.id.imageview_movie_poster);
        final TextView textViewMovieTitle = findViewById(R.id.textView_original_title);
        final TextView textViewMovieUserRating = findViewById(R.id.textview_user_rating);
        final TextView textViewMovieReleaseDate = findViewById(R.id.textview_release_date);
        final TextView textViewMoviePlotSynopsis = findViewById(R.id.textview_plot_synopsis);
        final Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra(PARAM_MOVIE)) {
            final Movie movie = intentThatStartedThisActivity.getParcelableExtra(PARAM_MOVIE);
            try {
                Picasso.with(this)
                        .load(NetworkUtils.createImageUri(movie.getPosterPath(), NetworkUtils.IMAGE_SIZE_BIG))
                        .into(imageViewMoviePoster);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            textViewMovieTitle.setText(getString(R.string.movie_detail_original_title, movie.getOriginalTitle()));
            textViewMovieUserRating.setText(getString(R.string.movie_detail_user_rating, movie.getUserRating()));
            textViewMovieReleaseDate.setText(getString(R.string.movie_detail_release_date, movie.getReleaseDate()));
            textViewMoviePlotSynopsis.setText(getString(R.string.movie_detail_plot_synopsis, movie.getPlotSynopsis()));
        }
    }

}
