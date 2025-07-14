package com.ssafy.moa2zi.member.dto.response;

public record MemberGetByNicknameResponse(
        Integer friendsOrder,
        Long memberId,
        String nickname,
        String profileImage

) {
}
