package com.ssafy.moa2zi.quiz.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuizWithRankResultResponse(
        Integer totalMemberCount,
        Integer myRanking,
        Long totalQuizCount,
        List<QuizWithRankingResponse> quizWithRankingResponseList
) {
}
