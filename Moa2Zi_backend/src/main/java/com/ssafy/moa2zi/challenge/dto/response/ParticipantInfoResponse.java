package com.ssafy.moa2zi.challenge.dto.response;

import com.ssafy.moa2zi.challenge.domain.Status;
import com.ssafy.moa2zi.member.domain.Gender;

public record ParticipantInfoResponse(
        Long challengeParticipantId,
        Long memberId,
        String nickname,
        String profileImage,
        Gender gender,
        Status status
) {
}
