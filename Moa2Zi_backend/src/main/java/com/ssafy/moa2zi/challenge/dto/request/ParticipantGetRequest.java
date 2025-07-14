package com.ssafy.moa2zi.challenge.dto.request;

import com.ssafy.moa2zi.challenge.domain.Status;

public record ParticipantGetRequest(
        Status status,
        Long next,
        Integer size
) {
    public ParticipantGetRequest {
        if(size == null || size == 0) {
            size = 10;
        }
    }
}
