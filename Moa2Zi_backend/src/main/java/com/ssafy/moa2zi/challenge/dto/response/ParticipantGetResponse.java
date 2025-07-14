package com.ssafy.moa2zi.challenge.dto.response;

import com.ssafy.moa2zi.challenge.domain.Status;

import java.util.List;

public record ParticipantGetResponse(
        List<ParticipantInfoResponse> participants,
        Long total,
        int size,
        boolean hasNext,
        Long next
) {
}
