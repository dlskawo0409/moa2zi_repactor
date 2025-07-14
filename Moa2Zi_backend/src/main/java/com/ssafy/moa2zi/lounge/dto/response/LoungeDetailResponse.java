package com.ssafy.moa2zi.lounge.dto.response;

import com.ssafy.moa2zi.lounge.domain.LoungeStatus;
import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
@Builder
public record LoungeDetailResponse(
        Long loungeId,
        String title,
        LoungeStatus loungeStatus,
        List<LoungeParticipantGetResponse> participantList,
        LocalDateTime createdAt
) {
}
