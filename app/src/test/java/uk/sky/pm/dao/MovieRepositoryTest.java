package uk.sky.pm.dao;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import uk.sky.pm.domain.Movie;
import uk.sky.pm.domain.Rating;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class MovieRepositoryTest {

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
        "postgres:17-alpine"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

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
        assertEquals("Alien", page.getContent().getFirst().name());
    }

    @Test
    void findAllMovies_shouldSortByRatingDescending() {
        var pageable = PageRequest.of(0, 3, Sort.by("movieRating").descending());
        var page = movieRepository.findAllMovies(pageable);

        var movies = page.getContent();
        assertEquals("Tenet", movies.getFirst().name());
        assertEquals(new BigDecimal(5).intValue(), movies.getFirst().movieRating().intValue());
        assertEquals("Interstellar", movies.get(1).name());
        assertEquals(new BigDecimal(4).intValue(), movies.get(1).movieRating().intValue());
        assertEquals("Alien", movies.getLast().name());
        assertEquals(new BigDecimal(3).intValue(), movies.getLast().movieRating().intValue());
    }

    private static Movie createMovie(final String name) {
        var movie = new Movie();
        movie.setName(name);
        return movie;
    }
}
