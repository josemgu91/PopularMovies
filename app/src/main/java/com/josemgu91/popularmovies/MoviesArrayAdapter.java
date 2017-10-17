package com.josemgu91.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;

/**
 * Created by jose on 10/17/17.
 */

public class MoviesArrayAdapter extends ArrayAdapter<Movie> {

    private Picasso picasso;

    public MoviesArrayAdapter(@NonNull final Context context) {
        super(context, 0);
        picasso = Picasso.with(getContext());
    }

    private static class MovieViewHolder {

        ImageView imageViewMoviePoster;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final View view;
        final MovieViewHolder movieViewHolder;
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(R.layout.element_movie, parent, false);
            movieViewHolder = new MovieViewHolder();
            movieViewHolder.imageViewMoviePoster = view.findViewById(R.id.imageview_movie_poster);
            view.setTag(movieViewHolder);
        } else {
            view = convertView;
            movieViewHolder = (MovieViewHolder) view.getTag();
        }
        final Movie movie = getItem(position);
        try {
            picasso
                    .load(NetworkUtils.createImageUri(movie.getPosterPath(), NetworkUtils.IMAGE_SIZE_SMALL))
                    .into(movieViewHolder.imageViewMoviePoster);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return view;
    }
}
