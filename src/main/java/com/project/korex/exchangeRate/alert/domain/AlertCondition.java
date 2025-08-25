package com.project.korex.exchangeRate.alert.domain;


public enum AlertCondition {
    ABOVE("이상"),
    BELOW("이하");

    private final String description;

    AlertCondition(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}