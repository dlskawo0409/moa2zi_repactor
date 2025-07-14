package com.ssafy.moa2zi.challenge.dto.request;

import com.ssafy.moa2zi.challenge.domain.Challenge;
import com.ssafy.moa2zi.challenge.domain.Status;

import java.time.LocalDateTime;

public record ChallengeSearchRequest(
        ChallengeSearchType type,
        String keyword,
        String tag,
        Status status,
        Long next,
        LocalDateTime lastStartTime,
        Integer size
) {
    public ChallengeSearchRequest {
        if (size == null || size == 0) {
            size = 10;
        }
    }
}
