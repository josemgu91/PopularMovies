package com.josemgu91.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

public class MoviesOverviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesCursorAdapter moviesCursorAdapter;

    private final static int LOADER_ID_MOVIES = 1;

    private final static String LOADER_ARG_KEY_MOVIE_FILTER = "movie_filter_by";
    private final static int LOADER_MOVIE_FILTER_MOST_POPULAR = 1;
    private final static int LOADER_MOVIE_FILTER_TOP_RATED = 2;
    private final static int LOADER_MOVIE_FILTER_FAVORITE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_overview);
        final GridView gridViewMovies = findViewById(R.id.gridview_movies);
        gridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(
                        new Intent(MoviesOverviewActivity.this, MovieDetailActivity.class)
                );
            }
        });
        moviesCursorAdapter = new MoviesCursorAdapter(this);
        gridViewMovies.setAdapter(moviesCursorAdapter);
        final Bundle defaultLoaderArguments = new Bundle();
        defaultLoaderArguments.putInt(LOADER_ARG_KEY_MOVIE_FILTER, LOADER_MOVIE_FILTER_MOST_POPULAR);
        getSupportLoaderManager().initLoader(LOADER_ID_MOVIES, defaultLoaderArguments, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String selection;
        switch (id) {
            case LOADER_ID_MOVIES:
                final int selectionArg = args.getInt(
                        LOADER_ARG_KEY_MOVIE_FILTER,
                        LOADER_MOVIE_FILTER_MOST_POPULAR);
                switch (selectionArg) {
                    default:
                    case LOADER_MOVIE_FILTER_MOST_POPULAR:
                        selection = MovieContract.MovieEntry.IS_MOST_POPULAR;
                        break;
                    case LOADER_MOVIE_FILTER_TOP_RATED:
                        selection = MovieContract.MovieEntry.IS_TOP_RATED;
                        break;
                    case LOADER_MOVIE_FILTER_FAVORITE:
                        selection = MovieContract.MovieEntry.IS_FAVORITE;
                        break;
                }
                return new CursorLoader(
                        this,
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        selection + "=?",
                        new String[]{"1"},
                        null
                );
            default:
                throw new RuntimeException(String.format("Loader with ID %s not Implemented", id));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        moviesCursorAdapter.swapCursor(data);
        if (data.getCount() == 0) {
            dispatchSync();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        moviesCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_order_most_popular:
                item.setChecked(true);
                fetchMostPopularMovies();
                return true;
            case R.id.action_order_top_rated:
                item.setChecked(true);
                fetchTopRatedMovies();
                return true;
            case R.id.action_favorites:
                item.setChecked(true);
                fetchFavoriteMovies();
            default:
                return false;
        }
    }

    private void fetchFavoriteMovies() {

    }

    private void fetchMostPopularMovies() {
    }

    private void fetchTopRatedMovies() {
    }

    private void dispatchSync() {
        startService(new Intent(this, MoviesSyncIntentService.class));
    }

}