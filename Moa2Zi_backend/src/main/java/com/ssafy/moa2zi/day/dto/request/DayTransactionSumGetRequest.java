package com.ssafy.moa2zi.day.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record DayTransactionSumGetRequest(

        @NotNull
        Long loungeId,

        @NotNull
        Long gameId,

        @NotNull
        Integer startTime,

        @NotNull
        Integer endTime
) {
}
