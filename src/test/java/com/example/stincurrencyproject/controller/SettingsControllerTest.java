package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.config.SecurityConfig;
import com.example.stincurrencyproject.model.UserSettings;
import com.example.stincurrencyproject.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SettingsController.class)
@Import(SecurityConfig.class)
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Test
    @WithMockUser(username = "uzivatel")
    void getSettings_WithAuth_ReturnsSettingsAsJson() throws Exception {
        UserSettings mockSettings = new UserSettings();
        mockSettings.setBaseCurrency("EUR");
        mockSettings.setSelectedCurrencies(List.of("CZK", "USD"));

        when(logService.loadUserSettings()).thenReturn(mockSettings);

        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseCurrency").value("EUR"))
                .andExpect(jsonPath("$.selectedCurrencies[0]").value("CZK"))
                .andExpect(jsonPath("$.selectedCurrencies[1]").value("USD"));
    }

    @Test
    @WithMockUser(username = "uzivatel")
    void saveSettings_WithAuth_ReturnsOk() throws Exception {
        String jsonBody = "{\"baseCurrency\":\"EUR\",\"selectedCurrencies\":[\"CZK\",\"USD\"]}";

        mockMvc.perform(post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonBody))
                .andExpect(status().isOk());

        verify(logService).saveUserSettings(any(UserSettings.class));
    }

    @Test
    void endpoints_WithoutAuth_ReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/settings"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/settings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }
}