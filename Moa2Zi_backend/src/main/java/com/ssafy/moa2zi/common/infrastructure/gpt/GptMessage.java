package com.ssafy.moa2zi.common.infrastructure.gpt;

public record GptMessage(
        String role,
        String content
) {
}
