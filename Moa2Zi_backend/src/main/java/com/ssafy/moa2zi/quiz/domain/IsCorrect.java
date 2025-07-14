package com.ssafy.moa2zi.quiz.domain;

public enum IsCorrect {
    YES,NO;


    public static IsCorrect fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Answer value cannot be null");
        }

        switch (value.trim().toLowerCase()) {
            case "yes":
                return YES;
            case "no":
                return NO;
            default:
                throw new IllegalArgumentException("Unknown answer value: " + value);
        }
    }
}
