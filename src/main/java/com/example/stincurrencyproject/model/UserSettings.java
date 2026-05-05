package com.example.stincurrencyproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class UserSettings {

    @JsonProperty("baseCurrency")
    private String baseCurrency;

    @JsonProperty("selectedCurrencies")
    private List<String> selectedCurrencies;

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public List<String> getSelectedCurrencies() {
        return selectedCurrencies;
    }

    public void setSelectedCurrencies(List<String> selectedCurrencies) {
        this.selectedCurrencies = selectedCurrencies;
    }
}