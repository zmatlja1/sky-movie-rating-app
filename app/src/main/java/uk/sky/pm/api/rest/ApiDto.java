package uk.sky.pm.api.rest;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface ApiDto extends Serializable {
}
