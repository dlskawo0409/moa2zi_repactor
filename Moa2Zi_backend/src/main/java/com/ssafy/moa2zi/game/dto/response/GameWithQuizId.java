package com.ssafy.moa2zi.game.dto.response;

import java.time.LocalDateTime;

public record GameWithQuizId(
        Long gameId,
        LocalDateTime createdAt,
        LocalDateTime endTime,
        Long quizId
) {
}
