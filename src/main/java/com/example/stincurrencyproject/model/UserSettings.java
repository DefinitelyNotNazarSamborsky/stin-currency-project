package com.example.stincurrencyproject.model;

import lombok.Data;
import java.util.List;

@Data
public class UserSettings {
    private String baseCurrency;
    private List<String> selectedCurrencies;
}
