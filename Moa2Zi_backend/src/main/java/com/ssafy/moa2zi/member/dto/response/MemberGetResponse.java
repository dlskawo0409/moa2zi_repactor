package com.ssafy.moa2zi.member.dto.response;

import com.ssafy.moa2zi.member.domain.Disclosure;
import com.ssafy.moa2zi.member.domain.Gender;
import com.ssafy.moa2zi.member.domain.Role;
import lombok.Builder;
import java.time.LocalDateTime;


// 회원조회API Response
@Builder
public record MemberGetResponse (
        Long memberId,
        String username,
        String nickname,
        LocalDateTime birthday,
        Gender gender,
        String profileImage,
        Boolean alarm,
        Disclosure disclosure,
        LocalDateTime createdAt,
        LocalDateTime updateAt,
        Boolean theyAreFriend
){}
