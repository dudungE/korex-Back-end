package com.project.korex.exchangeRate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "currency_rate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyRate {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "currency_seq_gen")
    @SequenceGenerator(name = "currency_seq_gen", sequenceName = "currency_seq", allocationSize = 1)
    private Long id;

    // 기준일
    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate;

    // 통화 코드 (예: USD, EUR)
    @Column(name = "cur_unit", length = 10, nullable = false)
    private String curUnit;

    // 기준 환율
    @Column(name = "base_rate")
    private BigDecimal baseRate;

    // 전일 대비 방향 (예: 상승, 하락)
    @Column(name = "change_direction")
    private String changeDirection;

    // 전일 대비 변화량
    @Column(name = "change_amount")
    private BigDecimal changeAmount;

    // 현찰 살 때 환율
    @Column(name = "buy_cash_rate")
    private BigDecimal buyCashRate;

    // 현찰 팔 때 환율
    @Column(name = "sell_cash_rate")
    private BigDecimal sellCashRate;

    // 송금 보낼 때
    @Column(name = "send_rate")
    private BigDecimal sendRate;

    // 송금 받을 때
    @Column(name = "receive_rate")
    private BigDecimal receiveRate;
}
