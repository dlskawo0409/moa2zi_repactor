package com.ssafy.moa2zi.chat.dto.request;

import com.ssafy.moa2zi.chat.domain.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

public record ChatSendRequest(
        @NotNull
        Long loungeId,
        Long memberId,

        String profileImage,
        String nickname,
        @NotNull
        MessageType messageType,
        @NotNull
        LocalDateTime localDateTime,
        String content
) {
}
