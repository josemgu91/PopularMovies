package com.josemgu91.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MoviesOverviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies_overview);
        new FetchMoviesAsyncTask().execute();
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
                return true;
            case R.id.action_order_top_rated:
                item.setChecked(true);
                return true;
            default:
                return false;
        }
    }

    private static class FetchMoviesAsyncTask extends AsyncTask<Integer, Void, List<Movie>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Movie> doInBackground(Integer... integers) {
            try {
                final String serverResponse = NetworkUtils.getPopularMovies();
                return ServerResponseParser.parseMovies(serverResponse);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                for (final Movie movie : movies) {
                    Log.d("MoviesOverviewActivity", movie.toString());
                }
            }
        }

    }

}
