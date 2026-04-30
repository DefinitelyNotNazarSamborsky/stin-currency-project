package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Map;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentRateResponse {
    private Boolean success;
    private Long timestamp;
    private String source;
    private Map<String, Double> quotes;
}