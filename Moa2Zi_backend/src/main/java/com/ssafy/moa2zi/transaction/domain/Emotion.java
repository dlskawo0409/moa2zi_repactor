package com.ssafy.moa2zi.transaction.domain;

import lombok.Getter;

@Getter
public enum Emotion {
    AWESOME("최고"),
    HAPPY("행복"),
    SURPRISE("놀람"),
    SAD("슬픔"),
    ANGRY("화남");

    private final String message;

    Emotion(String message) {
        this.message = message;
    }
}

