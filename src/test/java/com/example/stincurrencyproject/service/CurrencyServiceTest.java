package com.example.stincurrencyproject.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyServiceTest {

    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        currencyService = new CurrencyService();
    }

    @Test
    void findStrongestCurrency_ReturnsHighestValue() {
        Map<String, Double> quotes = Map.of(
                "USDGBP", 0.77,
                "USDCZK", 23.5,
                "USDEUR", 0.92
        );

        String strongest = currencyService.findStrongestCurrency(quotes);
        assertEquals("CZK", strongest);
    }

    @Test
    void findWeakestCurrency_ReturnsLowestValue() {
        Map<String, Double> quotes = Map.of(
                "USDGBP", 0.77,
                "USDCZK", 23.5,
                "USDEUR", 0.92
        );

        String weakest = currencyService.findWeakestCurrency(quotes);
        assertEquals("GBP", weakest);
    }

    @Test
    void calculateAverageRate_IgnoresMissingData() {
        Map<String, Map<String, Double>> historicRates = new HashMap<>();

        historicRates.put("2020-01-01", Map.of("USDGBP", 0.75, "USDCZK", 23.0));
        historicRates.put("2020-01-02", Map.of("USDCZK", 23.5));
        historicRates.put("2020-01-03", Map.of("USDGBP", 0.85, "USDCZK", 24.0));

        Double avgGbp = currencyService.calculateAverageRate(historicRates, "USDGBP");
        Double avgCzk = currencyService.calculateAverageRate(historicRates, "USDCZK");

        assertEquals(0.80, avgGbp, 0.001);
        assertEquals(23.5, avgCzk, 0.001);
    }

    @Test
    void handleEmptyOrNullInputs_ReturnsNull() {
        assertNull(currencyService.findStrongestCurrency(null));
        assertNull(currencyService.findStrongestCurrency(new HashMap<>()));
        assertNull(currencyService.findWeakestCurrency(null));
        assertNull(currencyService.calculateAverageRate(null, "USDGBP"));
    }
    @Test
    void findStrongestCurrency_ShortKeyLength_ReturnsOriginalKey() {
        Map<String, Double> quotes = Map.of(
                "GBP", 0.77,
                "CZK", 23.5 // Nejsilnější
        );
        assertEquals("CZK", currencyService.findStrongestCurrency(quotes));
    }
    @Test
    void findWeakestCurrency_ShortKeyLength_ReturnsOriginalKey() {
        Map<String, Double> quotes = Map.of(
                "GBP", 0.77, // Nejslabší
                "CZK", 23.5
        );
        assertEquals("GBP", currencyService.findWeakestCurrency(quotes));
    }
    @Test
    void findWeakestCurrency_EmptyMap_ReturnsNull() {
        assertNull(currencyService.findWeakestCurrency(new HashMap<>()));
    }
    @Test
    void calculateAverageRate_TargetCurrencyNotFound_ReturnsNull() {
        Map<String, Map<String, Double>> historicRates = new HashMap<>();
        historicRates.put("2020-01-01", Map.of("USDCZK", 23.0));

        assertNull(currencyService.calculateAverageRate(historicRates, "USDGBP"));
    }
    @Test
    void calculateAverageRate_NullDailyRates_AreSafelyIgnored() {
        Map<String, Map<String, Double>> historicRates = new HashMap<>();
        historicRates.put("2020-01-01", Map.of("USDCZK", 23.0));
        historicRates.put("2020-01-02", null);

        Double avgCzk = currencyService.calculateAverageRate(historicRates, "USDCZK");

        assertEquals(23.0, avgCzk, 0.001);
    }
    @Test
    void calculateAverageRate_EmptyMapOrNullTarget_ReturnsNull() {
        assertNull(currencyService.calculateAverageRate(new HashMap<>(), "USDGBP"));

        Map<String, Map<String, Double>> historicRates = new HashMap<>();
        historicRates.put("2020-01-01", Map.of("USDCZK", 23.0));
        assertNull(currencyService.calculateAverageRate(historicRates, null));
    }
}