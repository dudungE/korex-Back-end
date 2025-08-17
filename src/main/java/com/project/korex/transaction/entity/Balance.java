package com.project.korex.transaction.entity;

import com.project.korex.transaction.enums.AccountType;
import com.project.korex.user.entity.Users;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_code")
    private Currency currency;

    @Column(name = "available_amount", precision = 18, scale = 4)
    private BigDecimal availableAmount = BigDecimal.ZERO;

    @Column(name = "held_amount", precision = 18, scale = 4)
    private BigDecimal heldAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    private AccountType accountType;


    @Builder
    public Balance(Long id, Users user, Currency currency, BigDecimal availableAmount, BigDecimal heldAmount, AccountType accountType) {
        this.id = id;
        this.user = user;
        this.currency = currency;
        this.availableAmount = availableAmount;
        this.heldAmount = heldAmount;
        this.accountType = accountType;
    }
}
