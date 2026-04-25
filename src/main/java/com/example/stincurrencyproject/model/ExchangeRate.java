package com.example.stincurrencyproject.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExchangeRate {
    private String currency;
    private Double rate;
    private LocalDateTime date;
}
