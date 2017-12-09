package com.josemgu91.popularmovies;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by jose on 12/9/17.
 */

public class MoviesApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
