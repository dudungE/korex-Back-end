package com.project.korex.exchangeRate.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.korex.exchangeRate.dto.EximExchangeRateDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * 수출입은행 api으로 가져오는 코드
 * WebClient사용
 */
@Component
public class EximExchangeRateService {

    @Value("9SG123")
    private String authkey;

    @Value("A123")
    private String data;

    WebClient webClient;

    public JsonNode getExchangeDataSync(String searchdate) {

        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        // WebClient를 생성
        webClient = WebClient.builder().uriBuilderFactory(factory).build();

        // WebClient를 사용하여 동기적으로 데이터를 요청하고, 바로 parseJson 함수를 호출
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

    public List<EximExchangeRateDto> getExchangeDataAsDtoList(String searchdate) {
        JsonNode jsonNode = getExchangeDataSync(searchdate);

        if (jsonNode != null && jsonNode.isArray()) {
            List<EximExchangeRateDto> exchangeDTOList = new ArrayList<>();

            for (JsonNode node : jsonNode) {
                EximExchangeRateDto exchangeDTO = convertJsonToExchangeDto(node);
                exchangeDTOList.add(exchangeDTO);
            }

            return exchangeDTOList;
        }

        return Collections.emptyList();
    }

    public EximExchangeRateDto convertJsonToExchangeDto(JsonNode jsonNode) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.treeToValue(jsonNode, EximExchangeRateDto.class);
        } catch (JsonProcessingException e) {
            // 예외 처리 필요
            e.printStackTrace();
            return null;
        }
    }

}