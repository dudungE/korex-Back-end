package com.project.korex.exchangeRate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.korex.exchangeRate.dto.ExchangeRateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class ExchangeRateService {

    @Value("${exchange-authkey}")
    private String authkey;

    @Value("${exchange-data}")
    private String data;

//    private final String searchdate = getSearchdate();

    WebClient webClient;

    public JsonNode getExchangeDataSync(String searchdate) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        // WebClient를 생성합니다.
        webClient = WebClient.builder().uriBuilderFactory(factory).build();

        // WebClient를 사용하여 동기적으로 데이터를 요청하고, 바로 parseJson 함수를 호출합니다.
        String responseBody = webClient.get()
                .uri(builder -> builder
                        .scheme("https")
                        .host("oapi.koreaexim.go.kr")
                        .path("/site/program/financial/exchangeJSON")
                        .queryParam("authkey", authkey)
                        .queryParam("searchdate", searchdate)
                        .queryParam("data", data)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block(); // 동기적으로 결과를 얻음
        return parseJson(responseBody);
    }

    public JsonNode parseJson(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(responseBody);
        } catch (IOException e) {
            // 예외 처리 필요
            e.printStackTrace();
            return null;
        }
    }

    public List<ExchangeRateDto> getExchangeDataAsDtoList(String searchdate) {
        JsonNode jsonNode = getExchangeDataSync(searchdate);

        if (jsonNode != null && jsonNode.isArray()) {
            List<ExchangeRateDto> exchangeDTOList = new ArrayList<>();

            for (JsonNode node : jsonNode) {
                ExchangeRateDto exchangeDTO = convertJsonToExchangeDto(node);
                exchangeDTOList.add(exchangeDTO);
            }

            return exchangeDTOList;
        }

        return Collections.emptyList();
    }

    public ExchangeRateDto convertJsonToExchangeDto(JsonNode jsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode, ExchangeRateDto.class);
        } catch (JsonProcessingException e) {
            // 예외 처리 필요
            e.printStackTrace();
            return null;
        }
    }

//    // 토요일·일요일은 공식 환율 데이터가 제공되지 않음
//    public String getSearchdate() {
//
//        LocalDate currentDate = LocalDate.now();
//        DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
//        // 토요일
//        if (dayOfWeek.getValue() == 6)
//            return currentDate.minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        // 일요일
//        if (dayOfWeek.getValue() == 7)
//            return currentDate.minusDays(2).format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//
//        return currentDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//    }
}