package uk.sky.pm.api.rest.v1.dto;

import uk.sky.pm.api.rest.ApiDto;

public record MovieApiDto (String name, int rating) implements ApiDto {
}
