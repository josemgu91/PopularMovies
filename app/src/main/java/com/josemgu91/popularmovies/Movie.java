package com.josemgu91.popularmovies;

/**
 * Created by jose on 10/17/17.
 */

public class Movie {

    private final String id;
    private final String posterPath;
    private final String originalTitle;
    private final String plotSynopsis;
    private final String userRating;
    private final String releaseDate;

    public Movie(String id, String posterPath, String originalTitle, String plotSynopsis, String userRating, String releaseDate) {
        this.id = id;
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.plotSynopsis = plotSynopsis;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPlotSynopsis() {
        return plotSynopsis;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", posterPath='" + posterPath + '\'' +
                ", originalTitle='" + originalTitle + '\'' +
                ", plotSynopsis='" + plotSynopsis + '\'' +
                ", userRating='" + userRating + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                '}';
    }
}