package com.ssafy.moa2zi.chat.dto.response;

import com.ssafy.moa2zi.chat.domain.Chat;

import java.time.LocalDateTime;
import java.util.List;

public record ChatGetResponse(
        List<ChatWithMember> chatList,
        Long total,
        Integer size,
        Boolean hasNext,
        LocalDateTime next
) {
}
