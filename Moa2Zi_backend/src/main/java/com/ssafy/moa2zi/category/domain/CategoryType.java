package com.ssafy.moa2zi.category.domain;

import lombok.Getter;

@Getter
public enum CategoryType {
    INCOME("수익"),
    SPEND("지출");

    private final String message;

    CategoryType(String message) {
        this.message = message;
    }
}
