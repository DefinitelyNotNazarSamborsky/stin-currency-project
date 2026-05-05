package com.example.stincurrencyproject.service;

import com.example.stincurrencyproject.model.Logs;
import com.example.stincurrencyproject.model.UserSettings;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LogService {

    private final ObjectMapper objectMapper;
    private final String settingsFile;
    private final String logsFile;

    public LogService(ObjectMapper objectMapper,
                      @Value("${app.storage.path:.}") String storagePath) {
        this.objectMapper = objectMapper;
        this.settingsFile = storagePath + "/user_settings.json";
        this.logsFile = storagePath + "/application_logs.json";
    }

    public void saveUserSettings(UserSettings settings) {
        File file = new File(settingsFile);

        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            if (settings != null && settings.getBaseCurrency() != null) {
                objectMapper.writeValue(file, settings);
            } else {
                log.warn("Zápis nastavení byl zrušen - přijatá data jsou prázdná (null).");
            }
        } catch (Exception e) {
            log.error("Chyba při zápisu do souboru user_settings.json: {}", e.getMessage());
        }
    }

    public UserSettings loadUserSettings() {
        File file = new File(settingsFile);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, UserSettings.class);
            } catch (Exception e) {
                log.error("Nepodařilo se načíst nastavení, vracím výchozí: {}", e.getMessage());
            }
        }

        UserSettings defaultSettings = new UserSettings();
        defaultSettings.setBaseCurrency("USD");
        defaultSettings.setSelectedCurrencies(List.of("CZK", "EUR"));
        return defaultSettings;
    }

    public void saveLog(Logs logEntry) {
        File file = new File(logsFile);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        List<Logs> currentLogs = loadLogs();
        currentLogs.add(logEntry);
        try {
            objectMapper.writeValue(file, currentLogs);
            log.info("Chyba byla zapsána do logu.");
        } catch (Exception e) {
            log.error("Nepodařilo se uložit log: {}", e.getMessage());
        }
    }

    public List<Logs> loadLogs() {
        File file = new File(logsFile);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Logs.class));
            } catch (RuntimeException e) {
                log.error("Nepodařilo se načíst logy: {}", e.getMessage());
            }
        }
        return new ArrayList<>();
    }
}