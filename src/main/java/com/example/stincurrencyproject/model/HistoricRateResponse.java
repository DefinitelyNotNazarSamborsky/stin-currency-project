package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HistoricRateResponse{
        private Boolean success;
        private Boolean timeframe;
        private String source;
        @JsonProperty("start_date") private String startDate;
        @JsonProperty("end_date") private String endDate;
        @JsonProperty("quotes")
        private Map<String, Map<String, Double>> quotes;
}