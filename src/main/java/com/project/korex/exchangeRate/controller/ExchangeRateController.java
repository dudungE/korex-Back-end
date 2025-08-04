package com.project.korex.exchangeRate.controller;

import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import com.project.korex.exchangeRate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exchange")
@RequiredArgsConstructor
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

//    @GetMapping("/rates")
//    public List<ExchangeRateDto> getExchangeRates(
//            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().format(T(java.time.format.DateTimeFormatter).ofPattern('yyyyMMdd'))}") String searchdate
//    ) {
//        return exchangeRateService.getExchangeDataAsDtoList(searchdate);
//    }
}
