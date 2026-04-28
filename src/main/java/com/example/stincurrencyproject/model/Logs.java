package com.example.stincurrencyproject.model;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class Logs {
    private LocalDateTime timestamp;
    private Integer level;
    private String message;
}
