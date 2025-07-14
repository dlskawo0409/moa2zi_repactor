package com.ssafy.moa2zi.lounge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record LoungeCreateRequest(
        @NotBlank
        String title,

        @NotNull
        List<Long> participantList,

        @NotNull
        LocalDateTime endTime,
        Integer duration
) {
}
