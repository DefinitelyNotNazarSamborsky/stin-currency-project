package com.example.stincurrencyproject.service;

import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Map;

@Service
public class CurrencyService {

    public String findStrongestCurrency(Map<String, Double> quotes) {
        if (quotes == null || quotes.isEmpty()) {
            return null;
        }
        return Collections.max(quotes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public String findWeakestCurrency(Map<String, Double> quotes) {
        if (quotes == null || quotes.isEmpty()) {
            return null;
        }
        return Collections.min(quotes.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public Double calculateAverageRate(Map<String, Map<String, Double>> historicRates, String targetCurrency) {
        if (historicRates == null || historicRates.isEmpty() || targetCurrency == null) {
            return null;
        }

        double sum = 0.0;
        int count = 0;

        for (Map<String, Double> dailyRates : historicRates.values()) {
            if (dailyRates != null && dailyRates.containsKey(targetCurrency)) {
                sum += dailyRates.get(targetCurrency);
                count++;
            }
        }

        if (count == 0) {
            return null;
        }

        return sum / count;
    }
}