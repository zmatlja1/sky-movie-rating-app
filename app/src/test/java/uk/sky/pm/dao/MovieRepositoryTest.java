package uk.sky.pm.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import uk.sky.pm.domain.Movie;
import uk.sky.pm.domain.Rating;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MovieRepositoryTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @BeforeEach
    void init() {
        var movie1 = createMovie("Alien");
        var movie2 = createMovie("Interstellar");
        var movie3 = createMovie("Tenet");

        movieRepository.saveAll(List.of(movie1, movie2, movie3));

        ratingRepository.save(new Rating(5, null, movie3));
        ratingRepository.save(new Rating(4, null, movie2));
        ratingRepository.save(new Rating(3, null, movie1));
    }

    @Test
    void findAllMovies_sortName_paging() {
        var pageable = PageRequest.of(0, 2,
            Sort.by("name").ascending());

        var page = movieRepository.findAllMovies(pageable);

        assertAll(
            () -> assertEquals(2, page.getContent().size()),
            () -> assertEquals(3, page.getTotalElements()),
            () -> assertEquals("Alien", page.getContent().getFirst().name()),
            () -> assertEquals("Interstellar", page.getContent().getLast().name())
        );
    }

    @Test
    void findAllMovies_shouldReturnSecondPageSortedDescending() {
        var pageable = PageRequest.of(1, 2, Sort.by("name").descending());
        var page = movieRepository.findAllMovies(pageable);

        assertEquals(1, page.getContent().size());
        assertEquals("Tenet", page.getContent().getFirst().name());
    }

    @Test
    void findAllMovies_shouldSortByRatingDescending() {
        var pageable = PageRequest.of(0, 3, Sort.by("movieRating").descending());
        var page = movieRepository.findAllMovies(pageable);

        var movies = page.getContent();
        assertEquals("Tenet", movies.getFirst().name());
        assertEquals(new BigDecimal(5), movies.getFirst().movieRating());
        assertEquals("Interstellar", movies.get(1).name());
        assertEquals(new BigDecimal(4), movies.get(1).movieRating());
        assertEquals("Alien", movies.getLast().name());
        assertEquals(new BigDecimal(3), movies.getLast().movieRating());
    }

    private static Movie createMovie(final String name) {
        var movie = new Movie();
        movie.setName(name);
        return movie;
    }
}
