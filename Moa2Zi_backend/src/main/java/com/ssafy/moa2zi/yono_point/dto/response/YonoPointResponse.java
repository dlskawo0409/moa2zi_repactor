package com.ssafy.moa2zi.yono_point.dto.response;

import java.time.LocalDateTime;

public record YonoPointResponse(
        Long yonoPointId,
        Long pocketMoneyId,
        float score,
        LocalDateTime createdAt
) {
}
