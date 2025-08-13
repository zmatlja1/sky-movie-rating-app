package uk.sky.pm.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import uk.sky.pm.domain.Movie;
import uk.sky.pm.domain.Rating;
import uk.sky.pm.domain.User;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RatingRepositoryTest {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByMovieAndUser_shouldReturnTrue_whenRatingExists() {
        var movie = new Movie();
        movie.setName("Inception");
        movieRepository.save(movie);

        var user = new User();
        user.setEmail("test@example.com");
        userRepository.save(user);

        var rating = new Rating(5, user, movie);
        ratingRepository.save(rating);

        boolean exists = ratingRepository.existsByMovieAndUser(movie, user);

        assertTrue(exists);
    }

    @Test
    void existsByMovieAndUser_shouldReturnFalse_whenNoRatingExists() {
        var movie = new Movie();
        movie.setName("Interstellar");
        movieRepository.save(movie);

        var user = new User();
        user.setEmail("missing@example.com");
        userRepository.save(user);

        boolean exists = ratingRepository.existsByMovieAndUser(movie, user);

        assertFalse(exists);
    }

    @Test
    void findByMovieIdAndUserId_shouldReturnRating_whenExists() {
        var movie = new Movie();
        movie.setName("Tenet");
        movieRepository.save(movie);

        var user = new User();
        user.setEmail("user@example.com");
        userRepository.save(user);

        var rating = new Rating(4, user, movie);
        ratingRepository.save(rating);

        var result = ratingRepository.findByMovieIdAndUserId(movie.getId(), user.getId());

        assertTrue(result.isPresent());
        assertEquals(4, result.get().getRating());
    }

    @Test
    void findByMovieIdAndUserId_shouldReturnEmpty_whenNotExists() {
        var result = ratingRepository.findByMovieIdAndUserId(999L, 888L);
        assertTrue(result.isEmpty());
    }
}
