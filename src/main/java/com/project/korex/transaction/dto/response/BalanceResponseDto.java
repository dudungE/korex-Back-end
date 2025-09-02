package com.project.korex.transaction.dto.response;

import com.project.korex.transaction.entity.Balance;
import com.project.korex.transaction.entity.Currency;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceResponseDto {
    private String code;
    private String amount;
    private String name;
    private String flag;

    public static BalanceResponseDto from(Balance balance, Currency currency) {
        return new BalanceResponseDto(
                balance.getCurrency().getCode(),
                formatAmount(balance.getAvailableAmount()),
                currency.getCurrencyName(),
                getFlagForCountry(currency.getCountryName())
        );
    }

    private static String formatAmount(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amount);
    }

    private static String getFlagForCountry(String countryName) {
        Map<String, String> flags = Map.of(
                "한국", "🇰🇷",
                "미국", "🇺🇸",
                "유럽", "🇪🇺",
                "일본", "🇯🇵",
                "영국", "🇬🇧",
                "오스트레일리아", "🇦🇺",
                "캐나다", "🇨🇦",
                "스위스", "🇨🇭",
                "중국", "🇨🇳"
        );
        return flags.getOrDefault(countryName, "🌍");
    }
}

