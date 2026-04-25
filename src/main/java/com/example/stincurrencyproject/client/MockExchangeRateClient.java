package com.example.stincurrencyproject.client;

import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
@Profile("dev")
public class MockExchangeRateClient implements ExchangeRateClient {

    @Override
    public String getCurrentRates(String base, String symbols) {
        return readMockFile("mocks/current.json");
    }

    @Override
    public String getHistoricRates(String base, String symbols, String startDate, String endDate) {
        return readMockFile("mocks/historic.json");
    }

    private String readMockFile(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Chyba při načítání mock souboru: " + path, e);
        }
    }
}