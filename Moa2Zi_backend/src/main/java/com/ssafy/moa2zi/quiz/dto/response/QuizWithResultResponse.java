package com.ssafy.moa2zi.quiz.dto.response;

import com.ssafy.moa2zi.quiz.domain.IsCorrect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuizWithResultResponse {
    private Long quizId;
    private String content;
    private Boolean isCorrect;
    private IsCorrect submittedAnswer;
    private Long memberId;


}
