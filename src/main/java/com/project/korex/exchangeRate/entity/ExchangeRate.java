package com.project.korex.exchangeRate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
//@Table(name = "exchange_rate")
@Table(name = "exchange_rate", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"base_date", "cur_unit"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "exchange_generator")
    @SequenceGenerator(name = "exchange_generator", sequenceName = "exchange", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;  // 낙관적 락용 버전 필드 추가

    @Column(name = "cur_unit", length = 10)
    private String curUnit; // 통화코드

    @Column(name = "cur_nm", length = 50)
    private String curNm; // 국가/통화명

    @Column(name = "ttb", length = 20)
    private String ttb; // 전신환(송금) 받으실 때

    @Column(name = "tts", length = 20)
    private String tts; // 전신환(송금) 보내실 때

    @Column(name = "deal_bas_r", length = 20)
    private String dealBasR; // 매매 기준율

    @Column(name = "bkpr", length = 20)
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
    private LocalDate baseDate;  // 환율 기준 날짜
}
