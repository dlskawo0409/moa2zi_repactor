package com.ssafy.moa2zi.notification.dto;

import com.ssafy.moa2zi.member.domain.Member;

public record SenderInfo(
        Long senderId,
        String nickName,
        String profileImage
) {

    public static SenderInfo of(Member member) {
        if(member == null) return null;

        return new SenderInfo(
                member.getMemberId(),
                member.getNickname(),
                member.getProfileImage()
        );
    }
}
