package com.josemgu91.popularmovies;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

import org.json.JSONException;

import java.io.IOException;

/**
 * Created by jose on 12/9/17.
 */

public class MoviesSyncIntentService extends IntentService {

    public static final String PARAM_MOVIE_ID = "com.josemgu91.popularmovies.MOVIE_ID";

    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            if (intent.hasExtra(PARAM_MOVIE_ID)) {
                MovieDetailsSyncTask.sync(intent.getStringExtra(PARAM_MOVIE_ID), this);
            } else {
                MoviesSyncTask.sync(this);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
