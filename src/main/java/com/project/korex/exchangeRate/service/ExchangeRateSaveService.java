package com.project.korex.exchangeRate.service;

import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import com.project.korex.exchangeRate.entity.ExchangeRate;
import com.project.korex.exchangeRate.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRateSaveService {

    private final ExchangeRateRepository currencyRateRepository;
    private final ExchangeRateCrawlerService exchangeRateCrawlerService;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    /**
     * 크롤링해서 가져온 데이터를 DTO 변환 후 저장하는 메서드
     */
    @Transactional
    public void saveCurrencyRateDaily(String currencyCode, int page) throws Exception {
        // 1. 크롤링 데이터 가져오기 (List<Map<String,String>>)
        List<Map<String, String>> rawDataList = exchangeRateCrawlerService.crawlDailyRate(currencyCode, page);

        if (rawDataList == null || rawDataList.isEmpty()) {
            System.out.println("크롤링 데이터가 없습니다.");
            return;
        }

        // 2. 크롤링 데이터 -> DTO 변환
        List<ExchangeRateDto> dtoList = rawDataList.stream()
                .map(map -> mapToDto(map, currencyCode))
                .toList();

        // 3. 기준일 기준 중복 데이터 삭제
        LocalDate baseDate = dtoList.get(0).getBaseDate();
        currencyRateRepository.deleteByBaseDate(baseDate);

        // 4. DTO -> Entity 변환 및 저장
        List<ExchangeRate> entities = dtoList.stream()
                .map(this::dtoToEntity)
                .collect(Collectors.toList());

        currencyRateRepository.saveAll(entities);

        System.out.println("데이터 저장 완료: " + entities.size() + "건");
    }

    /**
     * Map<String, String> -> CurrencyRateDto 변환
     */
    private ExchangeRateDto mapToDto(Map<String, String> map, String currencyCode) {
        ExchangeRateDto dto = new ExchangeRateDto();

        // 날짜 변환
        String baseDateStr = map.get("base_date");
        LocalDate baseDate = LocalDate.parse(baseDateStr, dateFormatter);
        dto.setBaseDate(baseDate);

        dto.setCurrencyCode(currencyCode); // currencyCode가 외부에서 받아야 하면 파라미터로 바꾸셔야 합니다.

        // 숫자 필드는 BigDecimal로 변환, 변환 불가 시 null 처리
        dto.setBaseRate(parseBigDecimal(map.get("base_rate")));
        dto.setChangeDirection(map.get("change_direction"));
        dto.setChangeAmount(parseBigDecimal(map.get("change_amount")));
        dto.setBuyCashRate(parseBigDecimal(map.get("buy_cash_rate")));
        dto.setSellCashRate(parseBigDecimal(map.get("sell_cash_rate")));
        dto.setSendRate(parseBigDecimal(map.get("send_rate")));
        dto.setReceiveRate(parseBigDecimal(map.get("receive_rate")));

        return dto;
    }

    /**
     * DTO -> Entity 변환
     */
    private ExchangeRate dtoToEntity(ExchangeRateDto dto) {
        return ExchangeRate.builder()
                .baseDate(dto.getBaseDate())
                .currencyCode(dto.getCurrencyCode())
                .baseRate(dto.getBaseRate())
                .changeDirection(dto.getChangeDirection())
                .changeAmount(dto.getChangeAmount())
                .buyCashRate(dto.getBuyCashRate())
                .sellCashRate(dto.getSellCashRate())
                .sendRate(dto.getSendRate())
                .receiveRate(dto.getReceiveRate())
                .build();
    }

    /**
     * String -> BigDecimal 변환 (안전하게 처리)
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isEmpty() || value.equals("-")) {
            return null;
        }
        try {
            // 숫자에 쉼표가 포함되어 있을 수 있으니 제거 후 변환
            String sanitized = value.replaceAll(",", "");
            return new BigDecimal(sanitized);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}