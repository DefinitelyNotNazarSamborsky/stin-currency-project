package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.client.ExchangeRateClient;
import com.example.stincurrencyproject.model.CurrentRateResponse;
import com.example.stincurrencyproject.model.Logs;
import com.example.stincurrencyproject.service.LogService;
import com.example.stincurrencyproject.model.HistoricRateResponse;
import com.example.stincurrencyproject.service.CurrencyService;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Map;
@Slf4j
@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final ExchangeRateClient exchangeRateClient;
    private final CurrencyService currencyService;
    private final LogService logService;
    private final ObjectMapper objectMapper;

    public CurrencyController(ExchangeRateClient exchangeRateClient, CurrencyService currencyService, LogService logService, ObjectMapper objectMapper) {
        this.exchangeRateClient = exchangeRateClient;
        this.currencyService = currencyService;
        this.logService = logService;
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
            String errorDetail = String.format("Base: %s, Symbols: %s, Detail: %s", base, symbols, e.getMessage());
            log.error("API Error - Strongest | {}", errorDetail);

            Logs errorLog = new Logs();
            errorLog.setMessage("Chyba zjišťování nejsilnější měny | " + errorDetail);
            errorLog.setLevel(1);
            errorLog.setTimestamp(LocalDateTime.now());
            logService.saveLog(errorLog);
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
            String errorDetail = String.format("Base: %s, Symbols: %s, Detail: %s", base, symbols, e.getMessage());
            log.error("API Error - Weakest | {}", errorDetail);

            Logs errorLog = new Logs();
            errorLog.setMessage("Chyba zjišťování nejslabší měny | " + errorDetail);
            errorLog.setLevel(1);
            errorLog.setTimestamp(LocalDateTime.now());
            logService.saveLog(errorLog);
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
            log.info("Historic rates JSON: {}", json);
            HistoricRateResponse response = objectMapper.readValue(json, HistoricRateResponse.class);
            log.info("Parsed rates keys: {}", response.getQuotes());
            Double average = currencyService.calculateAverageRate(response.getQuotes(), base + targetCurrency);
            return ResponseEntity.ok(average);
        } catch (Exception e) {
            String errorDetail = String.format("Base: %s, Symbols: %s, Od: %s, Do: %s, Target: %s, Detail: %s",
                    base, symbols, startDate, endDate, targetCurrency, e.getMessage());
            log.error("API Error - Average | {}", errorDetail);

            Logs errorLog = new Logs();
            errorLog.setMessage("Chyba výpočtu průměru | " + errorDetail);
            errorLog.setLevel(1);
            errorLog.setTimestamp(LocalDateTime.now());
            logService.saveLog(errorLog);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/history")
    public ResponseEntity<List<Map<String, Object>>> getHistoryData(
            @RequestParam String base,
            @RequestParam String symbols,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            String json = exchangeRateClient.getHistoricRates(base, symbols, startDate, endDate);
            HistoricRateResponse response = objectMapper.readValue(json, HistoricRateResponse.class);

            List<Map<String, Object>> result = response.getQuotes().entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> {
                        Map<String, Object> point = new java.util.LinkedHashMap<>();
                        point.put("date", entry.getKey());
                        entry.getValue().forEach((currency, value) -> {
                            String shortName = currency.replace(base, "");
                            point.put(shortName, value);
                        });
                        return point;
                    })
                    .collect(java.util.stream.Collectors.toList());
            log.info("History chart result: {}", result);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("API Error - History | {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}