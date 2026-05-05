package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentRateResponse(
        Boolean success,
        Long timestamp,
        String source,
        @JsonProperty("quotes")
        Map<String, Double> quotes
) {}