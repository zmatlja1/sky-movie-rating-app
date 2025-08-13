package uk.sky.pm.service;

import uk.sky.pm.dto.MovieDto;

import java.util.List;

public interface MovieService {

    List<MovieDto> findMovies(int page, int size, String sortBy, boolean ascending);

    long addMovieRating(long movieId, int userRating);

    void updateMovieRating(long movieId, long ratingId, int userRating);

    void deleteMovieRating(long movieId, long ratingId);

}
