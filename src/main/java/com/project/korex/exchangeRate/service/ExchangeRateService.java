package com.project.korex.exchangeRate.service;

import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import com.project.korex.exchangeRate.entity.ExchangeRate;
import com.project.korex.exchangeRate.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final ExchangeRateRepository exchangeRateRepository;

    /**
     * 특정 날짜에 여러 통화코드에 해당하는 환율 데이터 조회
     */
    @Transactional(readOnly = true)
    public List<ExchangeRateDto> getExchangeRatesByDateAndCurrencies(LocalDate baseDate, List<String> currencyCodes) {
        List<ExchangeRate> rates = exchangeRateRepository.findByBaseDateAndCurrencyCodeIn(baseDate, currencyCodes);
        return rates.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 통화코드에 대해 일자별 환율 데이터 조회
     */
    @Transactional(readOnly = true)
    public List<ExchangeRateDto> getExchangeRatesByCurrencyOrderedByDate(String currencyCode) {
        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyCodeOrderByBaseDateAsc(currencyCode);
        return rates.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }


    /**
     * Entity -> DTO 변환 메서드
     */
    private ExchangeRateDto toDto(ExchangeRate entity) {
        ExchangeRateDto dto = new ExchangeRateDto();
        dto.setCurrencyCode(entity.getCurrencyCode());
        dto.setBaseDate(entity.getBaseDate());
        dto.setBaseRate(entity.getBaseRate());
        dto.setChangeDirection(entity.getChangeDirection());
        dto.setChangeAmount(entity.getChangeAmount());
        dto.setBuyCashRate(entity.getBuyCashRate());
        dto.setSellCashRate(entity.getSellCashRate());
        dto.setSendRate(entity.getSendRate());
        dto.setReceiveRate(entity.getReceiveRate());
        return dto;
    }

}
