package com.example.stincurrencyproject.client;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("!dev")
public class RealExchangeRateClient implements ExchangeRateClient {

    private final RestTemplate restTemplate;

    public RealExchangeRateClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String getCurrentRates(String base, String symbols) {
        String url = String.format("https://api.exchangerate.host/live?source=%s&currencies=%s", base, symbols);
        return restTemplate.getForObject(url, String.class);
    }

    @Override
    public String getHistoricRates(String base, String symbols, String startDate, String endDate) {
        String url = String.format("https://api.exchangerate.host/timeframe?start_date=%s&end_date=%s&source=%s&currencies=%s", startDate, endDate, base, symbols);
        return restTemplate.getForObject(url, String.class);
    }
}