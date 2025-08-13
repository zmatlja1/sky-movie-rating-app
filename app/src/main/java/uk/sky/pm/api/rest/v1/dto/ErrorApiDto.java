package uk.sky.pm.api.rest.v1.dto;

import uk.sky.pm.api.rest.ApiDto;

public record ErrorApiDto (String errorCode, String errorMessage) implements ApiDto {
}
