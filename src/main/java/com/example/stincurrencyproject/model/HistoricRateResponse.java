package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HistoricRateResponse(
        Boolean success,
        Boolean timeframe,
        String source,
        @JsonProperty("start_date") String startDate,
        @JsonProperty("end_date") String endDate,
        Map<String, Map<String, Double>> rates
) {}