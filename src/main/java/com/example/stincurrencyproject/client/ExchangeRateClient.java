package com.example.stincurrencyproject.client;

import org.springframework.cache.annotation.Cacheable;

public interface ExchangeRateClient {

    @Cacheable(value = "latestRates", key = "#base + '-' + #symbols")
    String getCurrentRates(String base, String symbols);

    @Cacheable(value = "historicRates", key = "#base + '-' + #symbols + '-' + #startDate + '-' + #endDate")
    String getHistoricRates(String base, String symbols, String startDate, String endDate);
}