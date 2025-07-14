package com.ssafy.moa2zi.chat.dto.response;

import com.ssafy.moa2zi.chat.domain.MessageType;
import lombok.Builder;

import java.time.LocalDateTime;
import java.time.LocalDateTime;

@Builder
public record ChatWithMember(
        String chatId,
        Long loungeId,
        Long memberId,
        String nickname,
        String profileImage,
        MessageType messageType,
        LocalDateTime timeStamp,
        String content
) {
}
