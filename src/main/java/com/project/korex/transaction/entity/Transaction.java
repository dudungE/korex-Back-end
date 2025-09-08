package com.project.korex.transaction.entity;

import com.project.korex.common.BaseEntity;
import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Transaction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private Users fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private Users toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_currency_code")
    private Currency fromCurrencyCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_currency_code")
    private Currency toCurrencyCode;

    @Column(name = "send_amount", precision = 18, scale = 4)
    private BigDecimal sendAmount;

    @Column(name = "receive_amount", precision = 18, scale = 4)
    private BigDecimal receiveAmount;

    @Column(name = "fee_percentage", precision = 5, scale = 4)
    private BigDecimal feePercentage;

    @Column(name = "exchange_rate_applied", precision = 18, scale = 8)
    private BigDecimal exchangeRateApplied;

    @Column(name = "fee_amount", precision = 18, scale = 4)
    private BigDecimal feeAmount;

    @Column(name = "total_deducted_amount", precision = 18, scale = 4)
    private BigDecimal totalDeductedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Builder.Default
    private String status = "COMPLETED";

}
