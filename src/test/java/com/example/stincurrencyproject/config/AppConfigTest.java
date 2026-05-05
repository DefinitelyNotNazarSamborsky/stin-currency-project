package com.example.stincurrencyproject.config;

import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class AppConfigTest {

    @Test
    void testAppConfigBeansAreCreated() {
        AppConfig appConfig = new AppConfig();

        RestTemplate restTemplate = appConfig.restTemplate();
        assertNotNull(restTemplate, "RestTemplate bean by neměl být null");

        CacheManager cacheManager = appConfig.cacheManager();
        assertNotNull(cacheManager, "CacheManager bean by neměl být null");
        assertNotNull(cacheManager.getCache("latestRates"), "Cache latestRates by měla existovat");
        assertNotNull(cacheManager.getCache("historicRates"), "Cache historicRates by měla existovat");
    }
}