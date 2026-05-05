package com.example.stincurrencyproject.config;

import com.example.stincurrencyproject.controller.SettingsController;
import com.example.stincurrencyproject.service.LogService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SettingsController.class)
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Test
    void unauthenticated_AjaxRequest_Returns401() throws Exception {
        mockMvc.perform(get("/api/settings")
                        .header("X-Requested-With", "XMLHttpRequest"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\":\"Unauthorized\"}"));
    }

    @Test
    void unauthenticated_JsonAcceptHeader_Returns401() throws Exception {
        mockMvc.perform(get("/api/settings")
                        .header("Accept", "application/json"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().json("{\"error\":\"Unauthorized\"}"));
    }

    @Test
    void unauthenticated_NoHeaders_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/settings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void unauthenticated_HtmlAcceptHeader_RedirectsToLogin() throws Exception {
        mockMvc.perform(get("/api/settings")
                        .header("Accept", "text/html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }
}