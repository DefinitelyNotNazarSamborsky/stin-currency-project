package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.model.UserSettings;
import com.example.stincurrencyproject.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final LogService logService;

    public SettingsController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping
    public ResponseEntity<UserSettings> getSettings() {
        return ResponseEntity.ok(logService.loadUserSettings());
    }

    @PostMapping
    public ResponseEntity<Void> saveSettings(@RequestBody UserSettings settings) {
        log.info("Ukládám nové nastavení uživatele. Základní měna: {}, Sledované měny: {}",
                settings.getBaseCurrency(), settings.getSelectedCurrencies());

        logService.saveUserSettings(settings);
        return ResponseEntity.ok().build();
    }

}