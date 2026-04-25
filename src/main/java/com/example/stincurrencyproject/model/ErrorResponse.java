package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ErrorResponse(
        boolean success,
        ErrorDetail error
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ErrorDetail(
            int code,
            String info
    ) {}
}