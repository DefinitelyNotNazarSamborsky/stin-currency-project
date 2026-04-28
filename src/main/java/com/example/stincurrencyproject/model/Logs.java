package com.example.stincurrencyproject.model;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Logs {
    private LocalDateTime timestamp;
    private int level;
    private String message;
}
