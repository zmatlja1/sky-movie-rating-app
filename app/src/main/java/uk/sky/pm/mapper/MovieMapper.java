package uk.sky.pm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.sky.pm.api.rest.v1.dto.MovieApiDto;
import uk.sky.pm.dto.MovieDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    List<MovieApiDto> mapApi(List<MovieDto> movies);

    @Mapping(target = "rating", source = "movieRating", qualifiedByName = "decToInt")
    MovieApiDto mapApi(MovieDto movie);

    @Named("decToInt")
    default int map(final BigDecimal decimal) {
        return Optional.ofNullable(decimal)
            .map(dec -> dec.setScale(0, RoundingMode.CEILING).intValue())
            .orElse(0);
    }

}
