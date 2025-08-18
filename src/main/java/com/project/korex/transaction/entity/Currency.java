package com.project.korex.transaction.entity;

import jakarta.persistence.*;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor

public class Currency {

    @Id
    @Column(length = 3, unique = true)
    private String code;

    @Column(name = "currency_name")
    private String currencyName;

    @Column(name = "display_color")
    private String displayColor;

    @Column(name = "decimal_places")
    private int decimalPlaces;

    @Column(name = "country_name")
    private String countryName;

    @Builder
    public Currency(String code, String currencyName, String displayColor, int decimalPlaces, String countryName) {
        this.code = code;
        this.currencyName = currencyName;
        this.displayColor = displayColor;
        this.decimalPlaces = decimalPlaces;
        this.countryName = countryName;
    }
}
