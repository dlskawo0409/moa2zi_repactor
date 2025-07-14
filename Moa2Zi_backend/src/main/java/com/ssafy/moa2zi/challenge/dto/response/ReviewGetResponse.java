package com.ssafy.moa2zi.challenge.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ReviewGetResponse(
        List<ReviewInfoResponse> reviewList,
        Long total,
        int size,
        boolean hasNext,
        Long next,
        LocalDateTime lastTime
) {
}
