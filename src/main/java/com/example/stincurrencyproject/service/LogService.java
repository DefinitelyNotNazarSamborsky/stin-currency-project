package com.example.stincurrencyproject.service;

import com.example.stincurrencyproject.model.Logs;
import com.example.stincurrencyproject.model.UserSettings;
import tools.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogService {

    private final ObjectMapper objectMapper;
    private final String SETTINGS_FILE = "user_settings.json";
    private final String LOGS_FILE = "application_logs.json";

    // Využití Dependency Injection k získání předkonfigurovaného mapperu ze Spring Bootu
    public LogService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Uloží uživatelské nastavení do souboru.
     */
    public void saveUserSettings(UserSettings settings) {
        try {
            objectMapper.writeValue(new File(SETTINGS_FILE), settings);
        } catch (RuntimeException e) {
            System.err.println("Nepodařilo se uložit nastavení: " + e.getMessage());
        }
    }

    /**
     * Načte uživatelské nastavení ze souboru. Pokud neexistuje, vrátí výchozí.
     */
    public UserSettings loadUserSettings() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, UserSettings.class);
            } catch (RuntimeException e) {
                System.err.println("Nepodařilo se načíst nastavení: " + e.getMessage());
            }
        }
        return new UserSettings(); // Vrací prázdné nastavení, pokud soubor chybí
    }

    /**
     * Přidá nový log do souboru s logy.
     */
    public void saveLog(Logs logEntry) {
        List<Logs> currentLogs = loadLogs();
        currentLogs.add(logEntry);
        try {
            objectMapper.writeValue(new File(LOGS_FILE), currentLogs);
        } catch (RuntimeException e) {
            System.err.println("Nepodařilo se uložit log: " + e.getMessage());
        }
    }

    /**
     * Načte všechny existující logy.
     */
    public List<Logs> loadLogs() {
        File file = new File(LOGS_FILE);
        if (file.exists()) {
            try {
                return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Logs.class));
            } catch (RuntimeException e) {
                System.err.println("Nepodařilo se načíst logy: " + e.getMessage());
            }
        }
        return new ArrayList<>();
    }
}