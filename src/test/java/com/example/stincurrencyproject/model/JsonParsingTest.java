package com.example.stincurrencyproject.model;

import com.example.stincurrencyproject.client.MockExchangeRateClient;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonParsingTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockExchangeRateClient mockClient = new MockExchangeRateClient();

    @Test
    void parseCurrentRates_SuccessfullyMapsJsonToObject() throws Exception {
        String json = mockClient.getCurrentRates("USD", "GBP");
        CurrentRateResponse response = objectMapper.readValue(json, CurrentRateResponse.class);

        assertTrue(response.getSuccess());
        assertEquals("USD", response.getSource());
        assertNotNull(response.getQuotes());
        assertEquals(0.7701, response.getQuotes().get("USDGBP"));
    }

    @Test
    void parseHistoricRates_SuccessfullyMapsJsonToObject() throws Exception {
        String json = mockClient.getHistoricRates("USD", "GBP,EUR", "2020-01-01", "2020-01-03");
        HistoricRateResponse response = objectMapper.readValue(json, HistoricRateResponse.class);

        assertTrue(response.getSuccess());
        assertTrue(response.getTimeframe());
        assertEquals("2020-01-01", response.getStartDate());
        assertEquals("2020-01-03", response.getEndDate());

        assertNotNull(response.getQuotes().get("2020-01-01"));
        assertEquals(0.7701, response.getQuotes().get("2020-01-01").get("USDGBP"));
        assertEquals(0.8119, response.getQuotes().get("2020-01-02").get("USDEUR"));
    }
}