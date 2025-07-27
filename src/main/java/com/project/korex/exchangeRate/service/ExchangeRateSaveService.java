package com.project.korex.exchangeRate.service;

import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import com.project.korex.exchangeRate.entity.ExchangeRate;
import com.project.korex.exchangeRate.repository.CurrencyRateRepository;
import com.project.korex.exchangeRate.repository.ExchangeRateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExchangeRateSaveService {

    private final ExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateService exchangeRateService;


    public ExchangeRateSaveService(ExchangeRateRepository exchangeRateRepository, ExchangeRateService exchangeRateService, ExchangeRateCrawlerService exchangeRateCrawlerService, CurrencyRateRepository currencyRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
        this.exchangeRateService = exchangeRateService;

    }

    /**
     * 특정 날짜 기준의 환율 데이터 리스트 저장
     * 기존 데이터가 있다면 삭제 후 저장하거나 업데이트 처리 가능
     */
    @Transactional
    public void saveExchangeRatesByDate(String baseDate) {

        // String → LocalDate 변환
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate parsedDate = LocalDate.parse(baseDate, formatter);

        // 기존 데이터 삭제 (optional) -> 예외처리하기
        exchangeRateRepository.deleteByBaseDate(parsedDate);

        // DTO 리스트 가져오기 (외부 API, 혹은 서비스 호출)
        List<ExchangeRateDto> dtoList = exchangeRateService.getExchangeDataAsDtoList(baseDate);

        // DTO → Entity 리스트 변환
        List<ExchangeRate> entityList = dtoList.stream()
                .map(dto -> convertDtoToEntity(dto, parsedDate))
                .collect(Collectors.toList());

        // 저장
        exchangeRateRepository.saveAll(entityList);
    }

    public List<ExchangeRate> findRatesByDate(LocalDate baseDate) {
        return exchangeRateRepository.findByBaseDate(baseDate);
    }

    private ExchangeRate convertDtoToEntity(ExchangeRateDto dto, LocalDate baseDate) {

        ExchangeRate entity = new ExchangeRate();

        entity.setBaseDate(baseDate);
        entity.setResult(dto.getResult());
        entity.setCurUnit(dto.getCur_unit());
        entity.setCurNm(dto.getCur_nm());
        entity.setTtb(dto.getTtb());
        entity.setTts(dto.getTts());
        entity.setDealBasR(dto.getDeal_bas_r());
        entity.setBkpr(dto.getBkpr());
        entity.setYyEfeeR(dto.getYy_efee_r());
        entity.setTenDdEfeeR(dto.getTen_dd_efee_r());
        entity.setKftcBkpr(dto.getKftc_bkpr());
        entity.setKftcDealBasR(dto.getKftc_deal_bas_r());

        return entity;
    }

}