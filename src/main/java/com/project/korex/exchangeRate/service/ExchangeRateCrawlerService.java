package com.project.korex.exchangeRate.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExchangeRateCrawlerService {

    private static final String URL = "https://finance.naver.com/marketindex/exchangeList.naver";

    public List<Map<String, String>> crawlExchangeRates() throws IOException {

        Document doc = Jsoup.connect(URL).userAgent("Mozilla/5.0").get();

        Elements rows = doc.select("table.tbl_exchange > tbody > tr");

        // JSON 배열로 만들기 위해 List<Map<String, String>>
        List<Map<String, String>> exchangeList = new ArrayList<>();

        // 컬럼명 정의
        String[] fieldNames = {
                "baseRate",     // 현재 환율
                "buyCashRate",      // 현찰 살 때
                "sellCashRate",     // 현찰 팔 때
                "sendRate",         // 송금 보낼 때
                "receiveRate",      // 송금 받을 때
                "usdConversionRate"  // 미화환산율
        };

        for (Element row : rows) {

            // 통화코드 추출
            String fullText = row.select("td.tit").text().trim();
            String currencyCode = fullText.replaceAll("[^A-Za-z]", "");

            Elements tds = row.select("td"); // 모든 td 요소

            Map<String, String> rateData = new LinkedHashMap<>();
            rateData.put("currencyCode", currencyCode);

            for (int i = 1; i <= 6; i++) {
                String key = fieldNames[i - 1];
                String value = tds.get(i).text().trim();
                rateData.put(key, value);
            }

            System.out.println(rateData);

            exchangeList.add(rateData);

        } // end for
        return exchangeList;
    }
}

