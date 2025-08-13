package uk.sky.pm.dao;

import org.springframework.data.repository.CrudRepository;
import uk.sky.pm.domain.Movie;
import uk.sky.pm.domain.Rating;
import uk.sky.pm.domain.User;

import java.util.Optional;

public interface RatingRepository extends CrudRepository<Rating, Long> {

    boolean existsByMovieAndUser(Movie movie, User user);

    Optional<Rating> findByMovieIdAndUserId(Long movieId, Long userId);

}
