package com.ssafy.moa2zi.lounge_participant.dto.response;

import lombok.Builder;

@Builder
public record LoungeParticipantGetResponse(
    Long memberId,
    String nickname,
    String profileImage
) {
}
