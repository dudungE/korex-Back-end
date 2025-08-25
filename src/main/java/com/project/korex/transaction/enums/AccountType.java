package com.project.korex.transaction.enums;

import lombok.Getter;

@Getter
public enum AccountType {
    KRW("원화계좌"),
    FOREIGN("외화계좌");

    private final String description;

    AccountType(String description) {
        this.description = description;
    }
}
