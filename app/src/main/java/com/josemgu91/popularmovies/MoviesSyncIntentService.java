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

    public MoviesSyncIntentService() {
        super("MoviesSyncIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            MoviesSyncTask.syncMovies(this);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
