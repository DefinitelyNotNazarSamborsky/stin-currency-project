package com.example.stincurrencyproject.client;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MockExchangeRateClientTest {

    private final MockExchangeRateClient client = new MockExchangeRateClient();

    @Test
    void getCurrentRates_ReturnsMockJson() {
        String result = client.getCurrentRates("USD", "GBP");

        assertNotNull(result, "Odpověď nesmí být null");
        assertTrue(result.contains("\"source\": \"USD\""), "Očekávaný JSON by měl obsahovat USD");
        assertTrue(result.contains("\"quotes\": {"), "JSON by měl obsahovat quotes");
    }

    @Test
    void getHistoricRates_ReturnsMockJson() {
        String result = client.getHistoricRates("USD", "GBP", "2020-01-01", "2020-01-03");

        assertNotNull(result, "Odpověď nesmí být null");
        assertTrue(result.contains("\"timeframe\": true"), "JSON by měl obsahovat timeframe flag");
    }

    @Test
    void readMockFile_WhenFileIsMissing_ThrowsRuntimeException() throws Exception {
        java.lang.reflect.Method method = MockExchangeRateClient.class.getDeclaredMethod("readMockFile", String.class);
        method.setAccessible(true);

        java.lang.reflect.InvocationTargetException exception = assertThrows(
                java.lang.reflect.InvocationTargetException.class,
                () -> method.invoke(client, "neexistujici_soubor.json")
        );
        Throwable cause = exception.getCause();
        assertTrue(cause instanceof RuntimeException);
        assertTrue(cause.getMessage().contains("Chyba při načítání mock souboru: neexistujici_soubor.json"));
    }
}