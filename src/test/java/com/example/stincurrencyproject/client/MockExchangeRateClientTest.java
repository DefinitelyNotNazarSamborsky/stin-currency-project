package com.example.stincurrencyproject.client;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MockExchangeRateClientTest {

    private final MockExchangeRateClient client = new MockExchangeRateClient();

    @Test
    void getCurrentRates_ReturnsMockJson() {
        String result = client.getCurrentRates("EUR", "CZK,USD");

        assertNotNull(result, "Odpověď nesmí být null");
        assertTrue(result.contains("\"base\": \"EUR\""), "Očekávaný JSON by měl obsahovat EUR");
        assertTrue(result.contains("\"success\": true"), "JSON by měl indikovat úspěch");
    }

    @Test
    void getHistoricRates_ReturnsMockJson() {
        String result = client.getHistoricRates("EUR", "CZK,USD", "2025-01-01", "2025-01-02");

        assertNotNull(result, "Odpověď nesmí být null");
        assertTrue(result.contains("\"timeseries\": true"), "JSON by měl obsahovat timeseries flag");
    }
}