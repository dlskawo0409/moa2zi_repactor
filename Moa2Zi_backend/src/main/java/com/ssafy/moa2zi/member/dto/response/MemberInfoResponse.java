package com.ssafy.moa2zi.member.dto.response;

import java.time.LocalDateTime;

public record MemberInfoResponse(
        Long memberId,
        String nickname,
        String profileImage,
        LocalDateTime createdAt
) {
}
