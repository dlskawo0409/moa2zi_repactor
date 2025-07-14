package com.ssafy.moa2zi.quiz.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record QuizWithResultSumResponse(
        Integer totalQuizCount,
        Integer rightAnswerCount,
        List<QuizWithResultResponse> quizWithResultResponseList
) {
}
