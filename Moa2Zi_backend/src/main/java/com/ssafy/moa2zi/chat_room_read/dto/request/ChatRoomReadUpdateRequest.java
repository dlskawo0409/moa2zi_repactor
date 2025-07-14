package com.ssafy.moa2zi.chat_room_read.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ChatRoomReadUpdateRequest(

        @NotNull
        Long loungeId,

        @NotNull
        LocalDateTime lastReadTime
) {
}
