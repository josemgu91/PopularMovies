package com.josemgu91.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by jose on 12/3/17.
 */

public class DatabaseManager extends SQLiteOpenHelper {

    private final static String DATABASE_NAME = "movies.db";
    private final static int DATABASE_VERSION = 2;

    private final Context context;

    public DatabaseManager(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String databaseSqlCreationScript;
        try {
            databaseSqlCreationScript = loadDatabaseSqlCreationScript();
            db.execSQL(databaseSqlCreationScript);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(db);
    }

    private String loadDatabaseSqlCreationScript() throws IOException {
        final InputStream inputStream = context.getResources().openRawResource(R.raw.movies);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final char[] buffer = new char[4096];
        int charactersRead = 0;
        final StringBuilder stringBuilder = new StringBuilder();
        while ((charactersRead = inputStreamReader.read(buffer)) != -1) {
            stringBuilder.append(buffer, 0, charactersRead);
        }
        return stringBuilder.toString();
    }
}
