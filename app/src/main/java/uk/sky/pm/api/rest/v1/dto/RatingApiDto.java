package uk.sky.pm.api.rest.v1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import uk.sky.pm.api.rest.ApiDto;

public record RatingApiDto (@Min(value = 1, message = "{RATING_MIN_VALUE}")
                            @Max(value = 5, message = "{RATING_MAX_VALUE}")
                            int rating) implements ApiDto {
}
