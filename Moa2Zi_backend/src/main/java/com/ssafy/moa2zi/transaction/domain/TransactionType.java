package com.ssafy.moa2zi.transaction.domain;

public enum TransactionType {
    INCOME("수익"),
    SPEND("지출"),
    TRANSFER("이체");

    private final String message;

    TransactionType(String message) {
        this.message = message;
    }
}

