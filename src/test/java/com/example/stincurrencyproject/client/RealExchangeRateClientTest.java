package com.example.stincurrencyproject.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RealExchangeRateClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RealExchangeRateClient client;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(client, "apiKey", "super-secret-key");
        ReflectionTestUtils.setField(client, "baseUrl", "http://api.exchangerate.com");
    }

    @Test
    void getCurrentRates_CallsRestTemplateWithCorrectUrl() {
        String expectedUrl = "http://api.exchangerate.com/live?access_key=super-secret-key&source=USD&currencies=CZK,GBP";
        String mockResponse = "{\"success\":true}";

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class))).thenReturn(mockResponse);

        String response = client.getCurrentRates("USD", "CZK,GBP");

        assertEquals(mockResponse, response, "Odpověď z klienta se musí shodovat s mockem");
    }

    @Test
    void getHistoricRates_CallsRestTemplateWithCorrectUrl() {
        String expectedUrl = "http://api.exchangerate.com/timeframe?access_key=super-secret-key&start_date=2020-01-01&end_date=2020-01-03&source=USD&currencies=CZK";
        String mockResponse = "{\"success\":true}";

        when(restTemplate.getForObject(eq(expectedUrl), eq(String.class))).thenReturn(mockResponse);

        String response = client.getHistoricRates("USD", "CZK", "2020-01-01", "2020-01-03");

        assertEquals(mockResponse, response, "Odpověď z klienta se musí shodovat s mockem");
    }
}