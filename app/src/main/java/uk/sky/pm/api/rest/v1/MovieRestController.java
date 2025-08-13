package uk.sky.pm.api.rest.v1;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uk.sky.pm.api.rest.v1.dto.MovieApiDto;
import uk.sky.pm.api.rest.v1.dto.RatingApiDto;
import uk.sky.pm.api.rest.v1.dto.RatingResponseApiDto;
import uk.sky.pm.mapper.MovieMapper;
import uk.sky.pm.service.MovieService;

import java.util.List;

@Tag(name = "Movies")
@AllArgsConstructor
@RestController
@RequestMapping("/rest/v1/movies")
public class MovieRestController {

    private final MovieService movieService;
    private final MovieMapper movieMapper;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MovieApiDto> findMovies(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10")
                                        @Max(value = 100, message = "{SIZE_MAX_VALUE}") int size,
                                        @RequestParam(defaultValue = "DEFAULT") String sortBy,
                                        @RequestParam(defaultValue = "true") boolean ascending) {
        return movieMapper.mapApi(movieService.findMovies(page, size, sortBy, ascending));
    }

    @PostMapping(path = "/{movieId}/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    public RatingResponseApiDto addRating(@PathVariable Long movieId, @RequestBody @Valid RatingApiDto ratingApiDto) {
        return new RatingResponseApiDto(movieService.addMovieRating(movieId, ratingApiDto.rating()));
    }

    @PutMapping(path = "/{movieId}/ratings/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRating(@PathVariable Long movieId, @PathVariable Long ratingId, @RequestBody @Valid RatingApiDto ratingApiDto) {
        movieService.updateMovieRating(movieId, ratingId, ratingApiDto.rating());
    }

    @DeleteMapping(path = "/{movieId}/ratings/{ratingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRating(@PathVariable Long movieId, @PathVariable Long ratingId) {
        movieService.deleteMovieRating(movieId, ratingId);
    }

}
