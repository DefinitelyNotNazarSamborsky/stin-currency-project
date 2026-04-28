package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.model.UserSettings;
import com.example.stincurrencyproject.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        logService.saveUserSettings(settings);
        return ResponseEntity.ok().build();
    }
}