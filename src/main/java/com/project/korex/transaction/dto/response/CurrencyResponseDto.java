package com.project.korex.transaction.dto.response;

import lombok.Data;

@Data
public class CurrencyResponseDto {

    private String currencyCode;
    private String currencyName;
    private String displayColor;
    private int decimalPlaces;
    private String countryName;
    private String flag;
}
