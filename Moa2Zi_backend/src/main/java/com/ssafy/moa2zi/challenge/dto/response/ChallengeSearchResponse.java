package com.ssafy.moa2zi.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ChallengeSearchResponse(
    List<ChallengeInfoResponse> challengeList,
    Long total,
    int size,
    boolean hasNext,
    Long next,
    LocalDateTime lastStartTime
) {
}
