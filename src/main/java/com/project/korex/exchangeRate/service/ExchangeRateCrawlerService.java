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

    private static final String URL_REALTIME = "https://finance.naver.com/marketindex/exchangeList.naver";
    private static final String URL_DAILY = "https://finance.naver.com/marketindex/exchangeDailyQuote.naver?marketindexCd=FX_%sKRW&page=%d";

    public List<Map<String, String>> crawlRealtimeRate() throws IOException {

        Document doc = Jsoup.connect(URL_REALTIME).userAgent("Mozilla/5.0").get();

        Elements rows = doc.select("table.tbl_exchange > tbody > tr");

        // JSON 배열로 만들기 위해 List<Map<String, String>>
        List<Map<String, String>> exchangeList = new ArrayList<>();

        // 컬럼명 정의
        String[] fieldNames = {
                "base_rate",     // 매매 기준율
                "buy_cash_rate",      // 현찰 살 때
                "sell_cash_rate",     // 현찰 팔 때
                "send_rate",         // 송금 보낼 때
                "receive_rate",      // 송금 받을 때
                "usd_conversion_rate"  // 미화환산율
        };

        for (Element row : rows) {

            // 통화코드 추출
            String fullText = row.select("td.tit").text().trim();
            String currencyCode = fullText.replaceAll("[^A-Za-z]", "");

            Elements tds = row.select("td"); // 모든 td 요소

            Map<String, String> rateData = new LinkedHashMap<>();
            rateData.put("currency_code", currencyCode);

            for (int i = 1; i <= 6; i++) {
                String key = fieldNames[i - 1];
                String value = tds.get(i).text().trim();
                rateData.put(key, value);
            }

            exchangeList.add(rateData);

        } // end for
        return exchangeList;
    }


    public List<Map<String, String>> crawlDailyRate(String currencyCode,int page) throws IOException {

        String url = String.format(URL_DAILY, currencyCode, page);
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

        Elements rows = doc.select("table.tbl_exchange > tbody > tr");
        System.out.println("jjh" + rows);

        // JSON 배열로 만들기 위해 List<Map<String, String>>
        List<Map<String, String>> exchangeList = new ArrayList<>();

        // 컬럼명 정의
        String[] fieldNames = {
                "base_date", // 날짜
                "base_rate",     // 매매 기준율
                "change_direction", // 전일대비 방향
                "change_amount",    // 전일대비 변화량
                "buy_cash_rate",      // 현찰 살 때
                "sell_cash_rate",     // 현찰 팔 때
                "send_rate",         // 송금 보낼 때
                "receive_rate",      // 송금 받을 때
        };

        for (Element row : rows) {
            Elements tds = row.select("td");
            // td 개수 체크 등 방어코드는 생략(필요가 있으면 추가)
            if (tds.size() < 7) continue; // 최소 td 개수 확인

            Map<String, String> rateData = new LinkedHashMap<>();

            // 0: base_date
            rateData.put(fieldNames[0], tds.get(0).text().trim());

            // 1: base_rate
            rateData.put(fieldNames[1], tds.get(1).text().trim());

            // 2: changeDirection / changeAmount
            Element diffTd = tds.get(2);
            String changeDirection = "";
            Element img = diffTd.selectFirst("img");
            if (img != null) {
                String alt = img.attr("alt"); // "상승" or "하락" 등
                changeDirection = alt;
            }
            rateData.put(fieldNames[2], changeDirection);

            // 전일대비 변화량은 숫자 부분의 텍스트만 추출
            String changeAmount = diffTd.ownText().trim();
            rateData.put(fieldNames[3], changeAmount);

            // 3~6: buy/sell/send/receive
            for (int i = 3; i <= 6; i++) {
                rateData.put(fieldNames[i + 1], tds.get(i).text().trim()); // i+1 : fieldNames 순서 맞추기
            }

            exchangeList.add(rateData);
        }

        System.out.println(exchangeList);

        return exchangeList;
    }



}

