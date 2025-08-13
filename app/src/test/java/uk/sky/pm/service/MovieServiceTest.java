package uk.sky.pm.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import uk.sky.pm.dao.MovieRepository;
import uk.sky.pm.dao.RatingRepository;
import uk.sky.pm.dao.UserRepository;
import uk.sky.pm.domain.Movie;
import uk.sky.pm.domain.Rating;
import uk.sky.pm.domain.User;
import uk.sky.pm.dto.MovieDto;
import uk.sky.pm.dto.UserDto;
import uk.sky.pm.exception.BusinessConflictException;
import uk.sky.pm.exception.BusinessNotFoundException;
import uk.sky.pm.mapper.MovieMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceImplTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private MovieServiceImpl movieService;

    @Captor
    private ArgumentCaptor<Rating> ratingCaptor;

    @Test
    void findMovies_shouldReturnPagedMovies() {
        var movieDto = mock(MovieDto.class);
        var page = new PageImpl<>(List.of(movieDto));
        when(movieRepository.findAllMovies(any(Pageable.class))).thenReturn(page);

        var result = movieService.findMovies(0, 10, "RATING", true);

        assertEquals(1, result.size());
        assertEquals(movieDto, result.getFirst());
        verify(movieRepository).findAllMovies(any(Pageable.class));
    }

    @Test
    void addMovieRating_shouldSaveCorrectRating() {
        var movie = new Movie();
        var rating = new Rating();
        rating.setId(10L);
        var user = new User();
        user.setEmail("test@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDto(1L, user.getEmail(), "123"));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(ratingRepository.existsByMovieAndUser(movie, user)).thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenReturn(rating);

        var newRatingId = movieService.addMovieRating(1L, 5);

        verify(ratingRepository).save(ratingCaptor.capture());
        var capturedRating = ratingCaptor.getValue();

        assertAll(
            () -> assertEquals(5, capturedRating.getRating()),
            () -> assertEquals(user, capturedRating.getUser()),
            () -> assertEquals(movie, capturedRating.getMovie()),
            () -> assertEquals(rating.getId(), newRatingId)
        );
    }

    @Test
    void addMovieRating_shouldThrowConflict_whenRatingExists() {
        var movie = new Movie();
        var user = new User();
        user.setEmail("test@example.com");

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDto(1L, user.getEmail(), "123"));
        when(movieRepository.findById(1L)).thenReturn(Optional.of(movie));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(ratingRepository.existsByMovieAndUser(movie, user)).thenReturn(true);

        assertThrows(BusinessConflictException.class,
            () -> movieService.addMovieRating(1L, 5));
    }

    @Test
    void updateMovieRating_shouldUpdateCorrectRating() {
        var user = new User();
        user.setId(42L);
        var rating = new Rating();
        rating.setRating(2);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDto(user.getId(), user.getEmail(), "123"));
        when(ratingRepository.findByMovieIdAndUserId(1L, user.getId())).thenReturn(Optional.of(rating));

        movieService.updateMovieRating(1L, 99L, 4);

        verify(ratingRepository).save(ratingCaptor.capture());
        var updatedRating = ratingCaptor.getValue();

        assertEquals(4, updatedRating.getRating());
    }

    @Test
    void updateMovieRating_shouldThrowNotFound_whenRatingMissing() {
        var user = new User();
        user.setId(42L);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new UserDto(user.getId(), user.getEmail(), "123"));
        when(ratingRepository.findByMovieIdAndUserId(1L, user.getId())).thenReturn(Optional.empty());

        assertThrows(BusinessNotFoundException.class,
            () -> movieService.updateMovieRating(1L, 99L, 4));
    }

    @Test
    void deleteMovieRating_shouldDeleteRating_whenExists() {
        var rating = new Rating();
        when(ratingRepository.findById(99L)).thenReturn(Optional.of(rating));

        movieService.deleteMovieRating(1L, 99L);

        verify(ratingRepository).delete(rating);
    }

    @Test
    void deleteMovieRating_shouldThrowNotFound_whenRatingMissing() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BusinessNotFoundException.class,
            () -> movieService.deleteMovieRating(1L, 99L));
    }
}
