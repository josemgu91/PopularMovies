package com.josemgu91.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.josemgu91.popularmovies.network.Movie;
import com.josemgu91.popularmovies.network.NetworkUtils;
import com.josemgu91.popularmovies.network.ServerResponseParser;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MoviesOverviewActivity extends AppCompatActivity {

    private MoviesArrayAdapter moviesArrayAdapter;
    private AsyncTask currentFetchMoviesAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_overview);
        final GridView gridViewMovies = findViewById(R.id.gridview_movies);
        moviesArrayAdapter = new MoviesArrayAdapter(this);
        gridViewMovies.setAdapter(moviesArrayAdapter);
        fetchMostPopularMovies();
        gridViewMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Movie movie = moviesArrayAdapter.getItem(position);
                startActivity(
                        new Intent(MoviesOverviewActivity.this, MovieDetailActivity.class)
                                .putExtra(MovieDetailActivity.PARAM_MOVIE, movie)
                );
            }
        });
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

    @Override
    protected void onDestroy() {
        cancelCurrentFetchTask();
        super.onDestroy();
    }

    private void fetchFavoriteMovies() {

    }

    private void fetchMostPopularMovies() {
        cancelCurrentFetchTask();
        currentFetchMoviesAsyncTask = new FetchMoviesAsyncTask().execute(FetchMoviesAsyncTask.PARAM_MOST_POPULAR);
    }

    private void fetchTopRatedMovies() {
        cancelCurrentFetchTask();
        currentFetchMoviesAsyncTask = new FetchMoviesAsyncTask().execute(FetchMoviesAsyncTask.PARAM_TOP_RATED);
    }

    private void cancelCurrentFetchTask() {
        if (currentFetchMoviesAsyncTask != null) {
            currentFetchMoviesAsyncTask.cancel(true);
            currentFetchMoviesAsyncTask = null;
        }
    }

    private class FetchMoviesAsyncTask extends AsyncTask<Integer, Void, List<Movie>> {

        public final static int PARAM_MOST_POPULAR = 0;
        public final static int PARAM_TOP_RATED = 1;

        @Override
        protected List<Movie> doInBackground(Integer... params) {
            try {
                final String serverResponse;
                if (params.length > 0) {
                    final int param = params[0];
                    if (param == PARAM_MOST_POPULAR) {
                        serverResponse = NetworkUtils.getPopularMovies();
                    } else {
                        serverResponse = NetworkUtils.getTopRatedMovies();
                    }
                } else {
                    serverResponse = NetworkUtils.getPopularMovies();
                }
                return ServerResponseParser.parseMovies(serverResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                moviesArrayAdapter.clear();
                moviesArrayAdapter.addAll(movies);
            }
        }

    }

}