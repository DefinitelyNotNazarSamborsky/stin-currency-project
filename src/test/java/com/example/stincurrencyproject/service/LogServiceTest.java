package com.example.stincurrencyproject.service;

import com.example.stincurrencyproject.model.Logs;
import com.example.stincurrencyproject.model.UserSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogServiceTest {

    private LogService logService;
    private final File settingsFile = new File("user_settings.json");
    private final File logsFile = new File("application_logs.json");

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        logService = new LogService(objectMapper);
        cleanUpFiles();
    }

    @AfterEach
    void tearDown() {
        cleanUpFiles();
    }

    private void cleanUpFiles() {
        if (settingsFile.exists()) settingsFile.delete();
        if (logsFile.exists()) logsFile.delete();
    }

    @Test
    void saveAndLoadUserSettings_SuccessfullyWorks() {
        UserSettings settings = new UserSettings();
        settings.setBaseCurrency("EUR");
        settings.setSelectedCurrencies(List.of("USD", "CZK"));

        logService.saveUserSettings(settings);
        assertTrue(settingsFile.exists(), "Soubor s nastavením by měl být vytvořen.");

        UserSettings loadedSettings = logService.loadUserSettings();
        assertEquals("EUR", loadedSettings.getBaseCurrency());
        assertTrue(loadedSettings.getSelectedCurrencies().contains("CZK"));
    }

    @Test
    void loadUserSettings_WhenFileMissing_ReturnsEmptySettings() {
        UserSettings loadedSettings = logService.loadUserSettings();
        assertNotNull(loadedSettings);
        assertNull(loadedSettings.getBaseCurrency(), "Pokud soubor neexistuje, baseCurrency by měla být null.");
    }

    @Test
    void saveAndLoadLogs_SuccessfullyWorks() {
        Logs log1 = new Logs();
        log1.setMessage("Test error 1");
        log1.setLevel(1);
        log1.setTimestamp(LocalDateTime.now());

        Logs log2 = new Logs();
        log2.setMessage("Test error 2");
        log2.setLevel(2);
        log2.setTimestamp(LocalDateTime.now());

        logService.saveLog(log1);
        logService.saveLog(log2);
        assertTrue(logsFile.exists(), "Soubor s logy by měl být vytvořen.");

        List<Logs> loadedLogs = logService.loadLogs();
        assertEquals(2, loadedLogs.size());
        assertEquals("Test error 1", loadedLogs.get(0).getMessage());
    }
}