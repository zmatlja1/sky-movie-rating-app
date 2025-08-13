package uk.sky.pm.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.sky.pm.api.rest.enums.SortBy;
import uk.sky.pm.dao.MovieRepository;
import uk.sky.pm.dao.RatingRepository;
import uk.sky.pm.dao.UserRepository;
import uk.sky.pm.domain.Rating;
import uk.sky.pm.dto.MovieDto;
import uk.sky.pm.enums.ErrorCode;
import uk.sky.pm.exception.BusinessConflictException;
import uk.sky.pm.exception.BusinessNotFoundException;
import uk.sky.pm.util.SecurityUser;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MovieServiceImpl implements MovieService {

    private MovieRepository movieRepository;
    private RatingRepository ratingRepository;
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    @Override
    public List<MovieDto> findMovies(int page, int size, String sortBy, boolean ascending) {
        var pageable = PageRequest.of(page, size,
            ascending ? getSort(sortBy).ascending() : getSort(sortBy).descending());
        var movies = movieRepository.findAllMovies(pageable);
        return movies.getContent();
    }

    private static Sort getSort(final String sortBy) {
        return Optional.ofNullable(sortBy)
            .filter(s -> SortBy.RATING.name().equals(sortBy))
            .map(s -> Sort.by(SortBy.RATING.getDbColumn()))
            .orElse(Sort.by(SortBy.DEFAULT.getDbColumn()));
    }

    @Transactional
    @Override
    public long addMovieRating(long movieId, int userRating) {
        var movie = movieRepository.findById(movieId)
            .orElseThrow(() -> new BusinessNotFoundException(ErrorCode.MOVIE_NOT_FOUND));

        var user = userRepository.findByEmail(SecurityUser.LOGGED_USER.get().getUsername())
            .orElseThrow(() -> new BusinessNotFoundException(ErrorCode.USER_NOT_FOUND));

        if (ratingRepository.existsByMovieAndUser(movie, user)) {
            throw new BusinessConflictException(ErrorCode.RATING_ALREADY_EXISTS_FOR_THE_MOVIE);
        }

        return ratingRepository.save(new Rating(userRating, user, movie)).getId();
    }

    @Transactional
    @Override
    public void updateMovieRating(long movieId, long ratingId, int userRating) {
        var rating = ratingRepository.findByMovieIdAndUserId(movieId, SecurityUser.LOGGED_USER.get().id())
            .orElseThrow(() -> new BusinessNotFoundException(ErrorCode.RATING_DOES_NOT_EXIST_FOR_THE_MOVIE));

        rating.setRating(userRating);
        ratingRepository.save(rating);
    }

    @Transactional
    @Override
    public void deleteMovieRating(long movieId, long ratingId) {
        var rating = ratingRepository.findById(ratingId)
            .orElseThrow(() -> new BusinessNotFoundException(ErrorCode.RATING_DOES_NOT_EXIST_FOR_THE_MOVIE));
        ratingRepository.delete(rating);
    }
}
