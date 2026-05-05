package com.example.stincurrencyproject.controller;

import com.example.stincurrencyproject.model.Logs;
import com.example.stincurrencyproject.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final LogService logService;

    public GlobalExceptionHandler(LogService logService) {
        this.logService = logService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllUncaughtExceptions(Exception ex) {
        log.error("Kritická chyba aplikace: {}", ex.getMessage(), ex);

        Logs errorLog = new Logs();
        errorLog.setTimestamp(LocalDateTime.now());
        errorLog.setLevel(500);
        errorLog.setMessage(ex.getMessage() != null ? ex.getMessage() : "Neznámá chyba");

        logService.saveLog(errorLog);

        Map<String, String> response = new HashMap<>();
        response.put("error", "Došlo k neočekávané chybě na serveru.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}