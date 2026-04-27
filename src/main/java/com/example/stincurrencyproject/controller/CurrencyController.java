package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.client.ExchangeRateClient;
import com.example.stincurrencyproject.model.CurrentRateResponse;
import com.example.stincurrencyproject.model.HistoricRateResponse;
import com.example.stincurrencyproject.service.CurrencyService;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final ExchangeRateClient exchangeRateClient;
    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;

    public CurrencyController(ExchangeRateClient exchangeRateClient, CurrencyService currencyService, ObjectMapper objectMapper) {
        this.exchangeRateClient = exchangeRateClient;
        this.currencyService = currencyService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/strongest")
    public ResponseEntity<String> getStrongestCurrency(@RequestParam String base, @RequestParam String symbols) {
        try {
            String json = exchangeRateClient.getCurrentRates(base, symbols);
            CurrentRateResponse response = objectMapper.readValue(json, CurrentRateResponse.class);

            String strongest = currencyService.findStrongestCurrency(response.quotes());
            return ResponseEntity.ok(strongest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/weakest")
    public ResponseEntity<String> getWeakestCurrency(@RequestParam String base, @RequestParam String symbols) {
        try {
            String json = exchangeRateClient.getCurrentRates(base, symbols);
            CurrentRateResponse response = objectMapper.readValue(json, CurrentRateResponse.class);

            String weakest = currencyService.findWeakestCurrency(response.quotes());
            return ResponseEntity.ok(weakest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/average")
    public ResponseEntity<Double> getAverageRate(
            @RequestParam String base,
            @RequestParam String symbols,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String targetCurrency) {
        try {
            String json = exchangeRateClient.getHistoricRates(base, symbols, startDate, endDate);
            HistoricRateResponse response = objectMapper.readValue(json, HistoricRateResponse.class);

            Double average = currencyService.calculateAverageRate(response.rates(), targetCurrency);
            return ResponseEntity.ok(average);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}