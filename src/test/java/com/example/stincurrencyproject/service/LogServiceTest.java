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
import java.util.ArrayList;
import java.util.List;

import org.mockito.Mockito;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

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

        logService = new LogService(objectMapper, ".");
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
    @Test
    void saveUserSettings_WhenExceptionThrown_LogsError() {
        // Arrange
        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        Mockito.doThrow(new RuntimeException("Simulovaná chyba zápisu"))
                .when(mockMapper).writeValue(any(File.class), any(UserSettings.class));
        LogService errorLogService = new LogService(mockMapper, ".");

        UserSettings settings = new UserSettings();

        // Act & Assert
        assertDoesNotThrow(() -> errorLogService.saveUserSettings(settings),
                "Metoda by měla výjimku zachytit a zalogovat, ne ji vyhodit dál.");
    }

    @Test
    void loadUserSettings_WhenExceptionThrown_ReturnsEmptySettings() throws IOException {

        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mockMapper.readValue(any(File.class), eq(UserSettings.class)))
                .thenThrow(new RuntimeException("Simulovaná chyba čtení"));
        LogService errorLogService = new LogService(mockMapper, ".");

        settingsFile.createNewFile();

        UserSettings result = errorLogService.loadUserSettings();

        assertNotNull(result);
        assertNull(result.getBaseCurrency(), "Při chybě by mělo být vráceno prázdné nastavení.");
    }

    @Test
    void saveLog_WhenExceptionThrown_LogsError() throws IOException {
        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mockMapper.readValue(any(File.class), any(tools.jackson.databind.JavaType.class)))
                .thenReturn(new ArrayList<Logs>());

        Mockito.doThrow(new RuntimeException("Simulovaná chyba zápisu logu"))
                .when(mockMapper).writeValue(any(File.class), any(List.class));

        LogService errorLogService = new LogService(mockMapper, ".");
        logsFile.createNewFile();

        // Act & Assert
        assertDoesNotThrow(() -> errorLogService.saveLog(new Logs()),
                "Metoda by měla výjimku zachytit a zalogovat, ne ji vyhodit dál.");
    }

    @Test
    void loadLogs_WhenExceptionThrown_ReturnsEmptyList() throws IOException {
        ObjectMapper mockMapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mockMapper.readValue(any(File.class), any(tools.jackson.databind.JavaType.class)))
                .thenThrow(new RuntimeException("Simulovaná chyba čtení logů"));
        LogService errorLogService = new LogService(mockMapper, ".");

        logsFile.createNewFile();

        List<Logs> result = errorLogService.loadLogs();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "Při chybě by měl být vrácen prázdný list logů.");
    }

    @Test
    void loadLogs_WhenFileMissing_ReturnsEmptyList() {
        if (logsFile.exists()) logsFile.delete();

        List<Logs> loadedLogs = logService.loadLogs();

        assertNotNull(loadedLogs);
        assertTrue(loadedLogs.isEmpty(), "Pokud soubor logů neexistuje, měl by se vrátit prázdný seznam.");
    }
}