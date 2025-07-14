package com.ssafy.moa2zi.lounge.dto.response;

import java.time.LocalDateTime;

public record LoungeWithGameAndParticipant(
        Long loungeId,
        String title,
        LocalDateTime createdAt,
        LocalDateTime loungeEndTime,
        LocalDateTime gameEndTime,
        Long memberId,
        String nickname,
        String profileImage
) {
}
