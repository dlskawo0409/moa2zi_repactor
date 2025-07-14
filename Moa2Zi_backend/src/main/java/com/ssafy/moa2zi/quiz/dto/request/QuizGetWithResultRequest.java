package com.ssafy.moa2zi.quiz.dto.request;

import jakarta.validation.constraints.NotNull;

public record QuizGetWithResultRequest(

        @NotNull
        Long memberId,

        @NotNull
        Long gameId
) {
}
