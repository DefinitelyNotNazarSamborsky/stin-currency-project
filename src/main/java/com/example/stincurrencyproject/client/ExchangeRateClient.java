package com.example.stincurrencyproject.client;

public interface ExchangeRateClient {
    String getCurrentRates(String base, String symbols);
    String getHistoricRates(String base, String symbols, String startDate, String endDate);
}
