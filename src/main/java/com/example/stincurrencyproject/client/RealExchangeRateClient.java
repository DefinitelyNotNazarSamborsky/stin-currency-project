package com.example.stincurrencyproject.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@Profile("prod")
@Slf4j
public class RealExchangeRateClient implements ExchangeRateClient {

    @Value("${exchange.api.key}")
    private String apiKey;

    @Value("${exchange.api.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;

    public RealExchangeRateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getCurrentRates(String base, String symbols) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/live")
                .queryParam("access_key", apiKey)
                .queryParam("source", base)
                .queryParam("currencies", symbols)
                .toUriString();
        log.info("Generated URL for API: {}", url);
        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public String getHistoricRates(String base, String symbols, String startDate, String endDate) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/timeframe")
                .queryParam("access_key", apiKey)
                .queryParam("start_date", startDate)
                .queryParam("end_date", endDate)
                .queryParam("source", base)
                .queryParam("currencies", symbols)
                .toUriString();
        log.info("Generated URL for API: {}", url);
        return restTemplate.getForObject(url, String.class);
    }
}