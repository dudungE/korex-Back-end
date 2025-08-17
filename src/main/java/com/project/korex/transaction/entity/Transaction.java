package com.project.korex.transaction.entity;

import com.project.korex.transaction.enums.TransactionType;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currencyCode;

    @Column(name = "send_amount", precision = 18, scale = 4)
    private BigDecimal sendAmount;

    @Column(name = "receive_amount", precision = 18, scale = 4)
    private BigDecimal receiveAmount;

    @Column(name = "exchange_rate_applied", precision = 18, scale = 8)
    private BigDecimal exchangeRateApplied;

    @Column(name = "fee_amount", precision = 18, scale = 4)
    private BigDecimal feeAmount;

    @Column(name = "total_deducted_amount", precision = 18, scale = 4)
    private BigDecimal totalDeductedAmount;

    @Column(name = "transaction_type")
    private TransactionType transactionType;

    private String status = "COMPLETED";


}
