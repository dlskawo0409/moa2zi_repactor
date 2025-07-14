package com.ssafy.moa2zi.finance.dto.member;

public record MemberUserKeyResponse(
        String userId,
        String userName,
        String institutionCode,
        String userKey,
        String created,
        String modified
) {
}
