package uk.sky.pm.api.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.sky.pm.api.rest.enums.SortBy;
import uk.sky.pm.api.rest.v1.MovieRestController;
import uk.sky.pm.api.rest.v1.dto.ErrorApiDto;
import uk.sky.pm.api.rest.v1.dto.MovieApiDto;
import uk.sky.pm.api.rest.v1.dto.RatingApiDto;
import uk.sky.pm.api.rest.v1.dto.RatingResponseApiDto;
import uk.sky.pm.config.WebSecurityConfiguration;
import uk.sky.pm.dto.MovieDto;
import uk.sky.pm.mapper.MovieMapper;
import uk.sky.pm.service.LocalizationServiceImpl;
import uk.sky.pm.service.MovieService;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {MovieRestController.class, MovieMapper.class, LocalizationServiceImpl.class, WebSecurityConfiguration.class})
public class MovieRestControllerTest {

    @MockitoBean
    private MovieService movieService;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Test
    void test_noAuthUser_returnMovies() throws Exception {
        var matrixMovie = new MovieDto("Matrix", BigDecimal.ONE);
        var alienMovie = new MovieDto("Alien", new BigDecimal(2));

        when(movieService.findMovies(anyInt(), anyInt(), eq(SortBy.DEFAULT.name()), anyBoolean()))
            .thenReturn(List.of(matrixMovie, alienMovie));

        var mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/rest/v1/movies")
            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

        var responseMovies = new ObjectMapper().readValue(mvcResult.getResponse().getContentAsString(),
            new TypeReference<List<MovieApiDto>>() {});

        var resultMatrix = responseMovies.stream()
            .filter(movie -> matrixMovie.name().equals(movie.name()))
            .findAny()
            .orElse(null);

        var resultAlien = responseMovies.stream()
            .filter(movie -> alienMovie.name().equals(movie.name()))
            .findAny()
            .orElse(null);

        assertNotNull(resultMatrix);
        assertNotNull(resultAlien);
        assertAll(
            () -> assertEquals(matrixMovie.name(), resultMatrix.name()),
            () -> assertEquals(matrixMovie.movieRating().intValue(), resultMatrix.rating()),
            () -> assertEquals(alienMovie.name(), resultAlien.name()),
            () -> assertEquals(alienMovie.movieRating().intValue(), resultAlien.rating())
        );
    }

    @Test
    void test_noAuthUser_returnMovies_invalidPageSize() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/rest/v1/movies")
                .param("size", "101")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isBadRequest());
    }

    @Test
    void test_noAuthUser_addRating() throws Exception {
        var ratingRequestApi = new RatingApiDto(5);

        mockMvc.perform(MockMvcRequestBuilders.post("/rest/v1/movies/1/ratings")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void test_loggedUser_addRating() throws Exception {
        var movieId = 1L;
        var newRatingId = 10L;
        var ratingRequestApi = new RatingApiDto(5);

        when(movieService.addMovieRating(eq(movieId), eq(ratingRequestApi.rating())))
            .thenReturn(newRatingId);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/rest/v1/movies/{movieId}/ratings", movieId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isCreated())
            .andReturn();

        var responseRating = new ObjectMapper().readValue(response.getResponse().getContentAsString(),
            RatingResponseApiDto.class);

        verify(movieService)
            .addMovieRating(longArgumentCaptor.capture(), integerArgumentCaptor.capture());

        assertAll(
            () -> assertEquals(movieId, longArgumentCaptor.getValue()),
            () -> assertEquals(ratingRequestApi.rating(), integerArgumentCaptor.getValue()),
            () -> assertEquals(newRatingId, responseRating.id()));
    }

    @Test
    @WithMockUser
    void test_loggedUser_addRating_wrongMinRating() throws Exception {
        var movieId = 1L;
        var ratingRequestApi = new RatingApiDto(0);

        var response = mockMvc.perform(MockMvcRequestBuilders
                .post("/rest/v1/movies/{movieId}/ratings", movieId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isBadRequest())
            .andReturn();

        var errorApiDto = new ObjectMapper()
            .readValue(response.getResponse().getContentAsString(), ErrorApiDto.class);

        assertEquals("INVALID_DATA", errorApiDto.errorCode());
    }

    @Test
    @WithMockUser
    void test_loggedUser_addRating_wrongMaxRating() throws Exception {
        var movieId = 1L;
        var ratingRequestApi = new RatingApiDto(10);

        var response = mockMvc.perform(MockMvcRequestBuilders.post("/rest/v1/movies/{movieId}/ratings", movieId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isBadRequest())
            .andReturn();

        var errorApiDto = new ObjectMapper()
            .readValue(response.getResponse().getContentAsString(), ErrorApiDto.class);

        assertEquals("INVALID_DATA", errorApiDto.errorCode());
    }

    @Test
    @WithMockUser
    void test_loggedUser_deleteRating() throws Exception {
        var movieId = 1L;
        var ratingId = 2L;

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/rest/v1/movies/{movieId}/ratings/{ratingId}", movieId, ratingId))
            .andExpect(status().isNoContent());

        verify(movieService)
            .deleteMovieRating(longArgumentCaptor.capture(), longArgumentCaptor.capture());

        assertAll(
            () -> assertEquals(movieId, longArgumentCaptor.getAllValues().getFirst()),
            () -> assertEquals(ratingId, longArgumentCaptor.getAllValues().getLast()));
    }

    @Test
    @WithMockUser
    void test_loggedUser_updateRating() throws Exception {
        var movieId = 1L;
        var ratingId = 2L;
        var ratingRequestApi = new RatingApiDto(5);

        mockMvc.perform(MockMvcRequestBuilders
                .put("/rest/v1/movies/{movieId}/ratings/{ratingId}", movieId, ratingId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isNoContent());

        verify(movieService)
            .updateMovieRating(longArgumentCaptor.capture(), longArgumentCaptor.capture(), integerArgumentCaptor.capture());

        assertAll(
            () -> assertEquals(movieId, longArgumentCaptor.getAllValues().getFirst()),
            () -> assertEquals(ratingId, longArgumentCaptor.getAllValues().getLast()),
            () -> assertEquals(ratingRequestApi.rating(), integerArgumentCaptor.getValue()));
    }

    @Test
    @WithMockUser
    void test_loggedUser_updateRating_wrongMinRating() throws Exception {
        var movieId = 1L;
        var ratingId = 2L;
        var ratingRequestApi = new RatingApiDto(0);

        var response = mockMvc.perform(MockMvcRequestBuilders
                .put("/rest/v1/movies/{movieId}/ratings/{ratingId}", movieId, ratingId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isBadRequest())
            .andReturn();

        var errorApiDto = new ObjectMapper()
            .readValue(response.getResponse().getContentAsString(), ErrorApiDto.class);

        assertEquals("INVALID_DATA", errorApiDto.errorCode());
    }

    @Test
    @WithMockUser
    void test_loggedUser_updateRating_wrongMaxRating() throws Exception {
        var movieId = 1L;
        var ratingId = 2L;
        var ratingRequestApi = new RatingApiDto(10);

        var response = mockMvc.perform(MockMvcRequestBuilders
                .put("/rest/v1/movies/{movieId}/ratings/{ratingId}", movieId, ratingId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(ratingRequestApi)))
            .andExpect(status().isBadRequest())
            .andReturn();

        var errorApiDto = new ObjectMapper()
            .readValue(response.getResponse().getContentAsString(), ErrorApiDto.class);

        assertEquals("INVALID_DATA", errorApiDto.errorCode());
    }
}
