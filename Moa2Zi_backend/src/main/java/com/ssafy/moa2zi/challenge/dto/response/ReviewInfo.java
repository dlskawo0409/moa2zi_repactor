package com.ssafy.moa2zi.challenge.dto.response;

import java.time.LocalDateTime;

public record ReviewInfo(
        Long challengeParticipantId,
        Long memberId,
        String review,
        LocalDateTime reviewedAt
) {
}
