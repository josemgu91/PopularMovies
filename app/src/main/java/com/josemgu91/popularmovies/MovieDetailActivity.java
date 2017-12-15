package com.josemgu91.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.josemgu91.popularmovies.network.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

/**
 * Created by jose on 10/13/17.
 */

public class MovieDetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    private final static int LOADER_ID_MOVIE_DETAIL = 1;
    private final static int LOADER_ID_MOVIE_VIDEOS = 2;
    private final static int LOADER_ID_MOVIE_REVIEWS = 3;

    public final static String PARAM_MOVIE_REMOTE_ID = "com.josemgu91.popularmovies.MOVIE_REMOTE_ID";

    private String movieRemoteId;
    private String movieId;

    private ImageView imageViewMoviePoster;
    private TextView textViewMovieTitle;
    private TextView textViewMovieUserRating;
    private TextView textViewMovieReleaseDate;
    private TextView textViewMoviePlotSynopsis;

    private Menu menu;

    private boolean isFavorite;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        imageViewMoviePoster = findViewById(R.id.imageview_movie_poster);
        textViewMovieTitle = findViewById(R.id.textView_original_title);
        textViewMovieUserRating = findViewById(R.id.textview_user_rating);
        textViewMovieReleaseDate = findViewById(R.id.textview_release_date);
        textViewMoviePlotSynopsis = findViewById(R.id.textview_plot_synopsis);
        final Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity.hasExtra(PARAM_MOVIE_REMOTE_ID)) {
            movieRemoteId = intentThatStartedThisActivity.getStringExtra(PARAM_MOVIE_REMOTE_ID);
            getSupportLoaderManager().initLoader(LOADER_ID_MOVIE_DETAIL, null, this);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean result = super.onPrepareOptionsMenu(menu);
        setFavoriteIcon(isFavorite);
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_switch_favorite:
                switchFavorite();
                return true;
            default:
                return false;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection;
        final String[] selectionArguments;
        final Uri uri;
        switch (id) {
            case LOADER_ID_MOVIE_DETAIL:
                uri = MovieContract.MovieEntry.CONTENT_URI;
                selection = MovieContract.MovieEntry.REMOTE_ID + "=?";
                selectionArguments = new String[]{movieRemoteId};
                break;
            case LOADER_ID_MOVIE_REVIEWS:
                uri = MovieContract.ReviewEntry.CONTENT_URI;
                selection = MovieContract.ReviewEntry.MOVIE_ID + "=?";
                selectionArguments = new String[]{movieId};
                break;
            case LOADER_ID_MOVIE_VIDEOS:
                uri = MovieContract.VideoEntry.CONTENT_URI;
                selection = MovieContract.VideoEntry.MOVIE_ID + "=?";
                selectionArguments = new String[]{movieId};
                break;
            default:
                throw new RuntimeException(String.format("Loader with ID %s not Implemented", id));
        }
        return new CursorLoader(
                this,
                uri,
                null,
                selection,
                selectionArguments,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID_MOVIE_DETAIL) {
            if (data.moveToNext()) {
                movieId = data.getString(data.getColumnIndex(MovieContract.MovieEntry._ID));
                final String posterPath = data.getString(data.getColumnIndex(MovieContract.MovieEntry.POSTER_REMOTE_ID));
                final String originalTitle = data.getString(data.getColumnIndex(MovieContract.MovieEntry.TITLE));
                final String userRating = data.getString(data.getColumnIndex(MovieContract.MovieEntry.USER_RATING));
                final String releaseDate = data.getString(data.getColumnIndex(MovieContract.MovieEntry.RELEASE_DATE));
                final String plotSynopsis = data.getString(data.getColumnIndex(MovieContract.MovieEntry.PLOT_SYNOPSIS));
                fillMovieDetails(posterPath, originalTitle, userRating, releaseDate, plotSynopsis);

                isFavorite = data.getInt(data.getColumnIndex(MovieContract.MovieEntry.IS_FAVORITE)) == 1;
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onRefresh() {
        refresh();
    }

    private void refresh() {

    }

    private void fillMovieDetails(final String posterPath,
                                  final String originalTitle,
                                  final String userRating,
                                  final String releaseDate,
                                  final String plotSynopsis) {
        try {
            Picasso.with(this)
                    .load(NetworkUtils.createImageUri(posterPath, NetworkUtils.IMAGE_SIZE_BIG))
                    .into(imageViewMoviePoster);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        textViewMovieTitle.setText(getString(R.string.movie_detail_original_title, originalTitle));
        textViewMovieUserRating.setText(getString(R.string.movie_detail_user_rating, userRating));
        textViewMovieReleaseDate.setText(getString(R.string.movie_detail_release_date, releaseDate));
        textViewMoviePlotSynopsis.setText(getString(R.string.movie_detail_plot_synopsis, plotSynopsis));
    }

    private void setFavoriteIcon(final boolean isFavorite) {
        final MenuItem menuItem = menu.findItem(R.id.action_switch_favorite);
        if (menuItem != null) {
            menuItem.setIcon(isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
        }
    }

    private void switchFavorite() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.IS_FAVORITE, isFavorite ? "0" : "1");
        getContentResolver().update(
                ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, Integer.valueOf(movieId)),
                contentValues,
                null,
                null
        );
        setFavoriteIcon(!isFavorite);
    }
}
