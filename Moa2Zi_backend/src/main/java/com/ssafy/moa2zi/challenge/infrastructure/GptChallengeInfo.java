package com.ssafy.moa2zi.challenge.infrastructure;

public record GptChallengeInfo(
        int challengeType,
        String challengeTitle,
        Integer period,
        Long amount,
        Integer limitCount,
        Integer percent,
        String category,
        String tags
) {
}
