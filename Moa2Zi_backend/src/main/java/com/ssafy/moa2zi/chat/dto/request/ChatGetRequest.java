package com.ssafy.moa2zi.chat.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ChatGetRequest(
        @NotNull
        Long loungeId,
        LocalDateTime next,
        Integer size
)
{
}
