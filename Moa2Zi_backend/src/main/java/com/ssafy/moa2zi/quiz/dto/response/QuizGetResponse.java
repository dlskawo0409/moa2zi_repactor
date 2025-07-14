package com.ssafy.moa2zi.quiz.dto.response;

import com.ssafy.moa2zi.lounge.dto.response.LoungeWithGame;
import com.ssafy.moa2zi.quiz.domain.IsCorrect;
import com.ssafy.moa2zi.quiz.domain.Quiz;
import lombok.Builder;

@Builder
public record QuizGetResponse(
    Long totalQuizSize,
    Long nowCount,
    Long nextQuizId,
    Quiz quiz,
    LoungeWithGame loungeWithGame

) {

}
