package com.project.korex.exchangeRate.service;

import com.project.korex.exchangeRate.entity.ExchangeRate;
import com.project.korex.exchangeRate.repository.ExchangeRateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExchangeRateSaveService {

    private final ExchangeRateRepository exchangeRateRepository;

    /**
     * 특정 날짜 기준의 환율 데이터 리스트 저장
     * 기존 데이터가 있다면 삭제 후 저장하거나 업데이트 처리 가능
     */
    @Transactional
    public void saveExchangeRatesByDate(LocalDate baseDate, List<ExchangeRate> rates) {
        // 1. 기존 해당 날짜 데이터 삭제 (필요시)
        exchangeRateRepository.deleteByBaseDate(baseDate);

        // 2. 저장할 데이터에 기준일 설정
//        rates.forEach(rate -> rate.setBaseDate(baseDate));

        // baseDate 설정과 함께 id, version 초기화 (version은 기본적으로 null 유지)
        rates.forEach(rate -> {
            rate.setBaseDate(baseDate);
            rate.setId(null);      // ID가 0 또는 잘못 들어온 경우 초기화
            // rate.setVersion(null); // 보통 version은 Setter 없애고 관리하는게 추천됨
        });

        // 3. 저장
        exchangeRateRepository.saveAll(rates);
    }

    /**
     * 특정 날짜 환율 데이터 조회 (예시)
     */
    public List<ExchangeRate> findRatesByDate(LocalDate baseDate) {
        return exchangeRateRepository.findByBaseDate(baseDate);
    }
}
