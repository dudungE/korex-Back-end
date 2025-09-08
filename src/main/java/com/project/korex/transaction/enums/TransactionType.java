package com.project.korex.transaction.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    TRANSFER("이체"),
    EXCHANGE("환전"),
    DEPOSIT("입금"),
    WITHDRAW("출금");


    private final String description;

    TransactionType(String description) {
        this.description = description;
    }
}
