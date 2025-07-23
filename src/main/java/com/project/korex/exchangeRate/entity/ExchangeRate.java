package com.project.korex.exchangeRate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exchange_rate", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"cur_unit"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_seq_gen")
    @SequenceGenerator(name = "exchange_seq_gen", sequenceName = "exchange_seq", allocationSize = 1)
    private Long id;

    private Integer result; // 결과

    @Column(name = "cur_unit", length = 10, nullable = false)
    private String curUnit; // 통화코드

    @Column(name = "cur_nm", length = 50)
    private String curNm; // 국가/통화명

    @Column(length = 20)
    private String ttb; // 전신환(송금) 받으실 때

    @Column(length = 20)
    private String tts; // 전신환(송금) 보내실 때

    @Column(name = "deal_bas_r", length = 20)
    private String dealBasR; // 매매 기준율

    @Column(length = 20)
    private String bkpr; // 장부가격

    @Column(name = "yy_efee_r", length = 20)
    private String yyEfeeR; // 년환가료율

    @Column(name = "ten_dd_efee_r", length = 20)
    private String tenDdEfeeR; // 10일환가료율

    @Column(name = "kftc_bkpr", length = 20)
    private String kftcBkpr; // 서울외국환중개 매매기준율

    @Column(name = "kftc_deal_bas_r", length = 20)
    private String kftcDealBasR; // 서울외국환중개장부가격

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate; // 환율 기준 날짜
}

