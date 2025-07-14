package com.ssafy.moa2zi.finance.dto.member;

import lombok.Builder;

@Builder
public record MemberUserKeyRequest(
        String apiKey,
        String userId // 이메일
) {
}
