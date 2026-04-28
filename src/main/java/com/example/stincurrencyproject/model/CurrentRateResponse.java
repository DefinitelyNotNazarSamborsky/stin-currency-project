package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CurrentRateResponse(
        Boolean success,
        Long timestamp,
        String source,
        Map<String, Double> quotes
) {}