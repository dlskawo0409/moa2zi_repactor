package com.ssafy.moa2zi.quiz.dto.response;

public record QuizWithRankingResponse(
        Long memberId,
        String nickname,
        Integer correctCount
) {
}
