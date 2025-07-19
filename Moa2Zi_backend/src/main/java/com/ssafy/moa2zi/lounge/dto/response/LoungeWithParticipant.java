package com.ssafy.moa2zi.lounge.dto.response;

import lombok.Builder;

@Builder
public record LoungeWithParticipant(
        Long loungeId,
        Long memberId,
        String nickname,
        String profileImage
) {
}
