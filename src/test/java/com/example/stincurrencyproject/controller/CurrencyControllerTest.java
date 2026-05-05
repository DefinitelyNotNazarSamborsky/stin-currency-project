package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.client.ExchangeRateClient;
import com.example.stincurrencyproject.config.SecurityConfig;
import com.example.stincurrencyproject.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.example.stincurrencyproject.service.LogService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrencyController.class)
@Import(SecurityConfig.class)
class CurrencyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExchangeRateClient exchangeRateClient;

    @MockitoBean
    private CurrencyService currencyService;

    @MockitoBean
    private LogService logService;

    @Test
    @WithMockUser(username = "uzivatel")
    void getStrongestCurrency_WithAuth_ReturnsOk() throws Exception {
        String mockJson = "{\"success\":true,\"timestamp\":1698739200,\"source\":\"USD\",\"quotes\":{\"USDCZK\":24.5}}";
        when(exchangeRateClient.getCurrentRates("USD", "CZK")).thenReturn(mockJson);
        when(currencyService.findStrongestCurrency(any())).thenReturn("USDCZK");

        mockMvc.perform(get("/api/currencies/strongest")
                        .param("base", "USD")
                        .param("symbols", "CZK"))
                .andExpect(status().isOk())
                .andExpect(content().string("USDCZK"));
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getWeakestCurrency_WithAuth_ReturnsOk() throws Exception {
        // Doplněno pole 'timestamp'
        String mockJson = "{\"success\":true,\"timestamp\":1698739200,\"source\":\"USD\",\"quotes\":{\"USDCZK\":24.5, \"USDGBP\":0.77}}";
        when(exchangeRateClient.getCurrentRates("USD", "CZK,GBP")).thenReturn(mockJson);
        when(currencyService.findWeakestCurrency(any())).thenReturn("USDGBP");

        mockMvc.perform(get("/api/currencies/weakest")
                        .param("base", "USD")
                        .param("symbols", "CZK,GBP"))
                .andExpect(status().isOk())
                .andExpect(content().string("USDGBP"));
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getAverageRate_WithAuth_ReturnsOk() throws Exception {
        String mockJson = "{\"success\":true,\"timeframe\":true,\"source\":\"USD\",\"start_date\":\"2020-01-01\",\"end_date\":\"2020-01-03\",\"rates\":{\"2020-01-01\":{\"USDCZK\":24.0}}}";
        when(exchangeRateClient.getHistoricRates("USD", "CZK", "2020-01-01", "2020-01-03")).thenReturn(mockJson);
        when(currencyService.calculateAverageRate(any(), eq("USDCZK"))).thenReturn(24.0);

        mockMvc.perform(get("/api/currencies/average")
                        .param("base", "USD")
                        .param("symbols", "CZK")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-03")
                        .param("targetCurrency", "CZK"))
                .andExpect(status().isOk())
                .andExpect(content().string("24.0"));
    }

    @Test
    void getStrongestCurrency_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/currencies/strongest")
                        .param("base", "USD")
                        .param("symbols", "CZK"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getStrongestCurrency_WhenApiThrows_Returns500() throws Exception {
        when(exchangeRateClient.getCurrentRates(any(), any()))
                .thenThrow(new RuntimeException("API nedostupné"));

        mockMvc.perform(get("/api/currencies/strongest")
                        .param("base", "USD")
                        .param("symbols", "CZK"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getWeakestCurrency_WhenApiThrows_Returns500() throws Exception {
        when(exchangeRateClient.getCurrentRates(any(), any()))
                .thenThrow(new RuntimeException("API nedostupné"));

        mockMvc.perform(get("/api/currencies/weakest")
                        .param("base", "USD")
                        .param("symbols", "CZK"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getAverageRate_WhenApiThrows_Returns500() throws Exception {
        when(exchangeRateClient.getHistoricRates(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("API nedostupné"));

        mockMvc.perform(get("/api/currencies/average")
                        .param("base", "USD")
                        .param("symbols", "CZK")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-03")
                        .param("targetCurrency", "CZK"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getHistoryData_WithAuth_ReturnsOk() throws Exception {
        String mockJson = "{\"success\":true,\"timeframe\":true,\"source\":\"USD\",\"start_date\":\"2020-01-01\",\"end_date\":\"2020-01-03\",\"quotes\":{\"2020-01-01\":{\"USDCZK\":24.0}}}";
        when(exchangeRateClient.getHistoricRates(any(), any(), any(), any())).thenReturn(mockJson);

        mockMvc.perform(get("/api/currencies/history")
                        .param("base", "USD")
                        .param("symbols", "CZK")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-03"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getHistoryData_WhenApiThrows_Returns500() throws Exception {
        when(exchangeRateClient.getHistoricRates(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("chyba"));

        mockMvc.perform(get("/api/currencies/history")
                        .param("base", "USD")
                        .param("symbols", "CZK")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-03"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getHistoryData_WithMultipleCurrencies_ReturnsOk() throws Exception {
        String mockJson = "{\"success\":true,\"timeframe\":true,\"source\":\"USD\",\"start_date\":\"2020-01-01\",\"end_date\":\"2020-01-01\",\"quotes\":{\"2020-01-01\":{\"USDCZK\":24.0, \"USDGBP\":0.77}}}";
        when(exchangeRateClient.getHistoricRates(any(), any(), any(), any())).thenReturn(mockJson);

        mockMvc.perform(get("/api/currencies/history")
                        .param("base", "USD")
                        .param("symbols", "CZK,GBP")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-01"))
                .andExpect(status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].date").value("2020-01-01"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].CZK").value(24.0))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$[0].GBP").value(0.77));
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void getHistoryData_WithEmptyQuotes_ReturnsEmptyArray() throws Exception {
        String mockJson = "{\"success\":true,\"timeframe\":true,\"source\":\"USD\",\"start_date\":\"2020-01-01\",\"end_date\":\"2020-01-03\",\"quotes\":{}}";
        when(exchangeRateClient.getHistoricRates(any(), any(), any(), any())).thenReturn(mockJson);

        mockMvc.perform(get("/api/currencies/history")
                        .param("base", "USD")
                        .param("symbols", "CZK")
                        .param("startDate", "2020-01-01")
                        .param("endDate", "2020-01-03"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

}