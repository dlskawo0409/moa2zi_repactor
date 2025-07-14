package com.ssafy.moa2zi.answer.dto.request;

import com.ssafy.moa2zi.quiz.domain.IsCorrect;

import jakarta.validation.constraints.NotNull;

public record AnswerCreateRequest(

	@NotNull
	Long quizId,

	@NotNull
	IsCorrect submission

) {
}
