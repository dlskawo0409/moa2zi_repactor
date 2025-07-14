package com.ssafy.moa2zi.yono_point.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record YonoPointSearchResponse(
        Float monthScore,
        List<dayScore> dayScoreList
) {
    public record dayScore(
            Float score,
            LocalDateTime createdAt
    ) {}
}
