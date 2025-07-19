package com.ssafy.moa2zi.auth.dto.response;

import lombok.Builder;

@Builder
public record AccessAndRefreshToken(
        String accessToken,
        String refreshToken
) {
}
