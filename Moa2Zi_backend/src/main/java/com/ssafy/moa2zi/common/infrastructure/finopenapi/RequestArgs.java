package com.ssafy.moa2zi.common.infrastructure.finopenapi;

import lombok.Builder;

@Builder
public record RequestArgs(
        String apiName,
        String apiServiceCode,
        String apiKey,
        String userKey
) {
}
