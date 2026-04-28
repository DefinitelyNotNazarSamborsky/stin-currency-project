package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorResponse(
        Boolean success,
        ErrorDetail error
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ErrorDetail(
            Integer code,
            String info
    ) {}
}