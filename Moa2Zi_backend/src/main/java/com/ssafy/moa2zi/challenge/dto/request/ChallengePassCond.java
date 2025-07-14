package com.ssafy.moa2zi.challenge.dto.request;

import lombok.Builder;

@Builder
public record ChallengePassCond(
        int challengeType,
        Long amount, // 금액 제한
        Integer limitCount, // 횟수 제한
        Integer percent,
        String categoryName
) {
}
