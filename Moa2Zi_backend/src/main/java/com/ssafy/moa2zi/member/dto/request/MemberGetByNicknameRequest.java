package com.ssafy.moa2zi.member.dto.request;

import com.mongodb.lang.Nullable;
import jakarta.validation.constraints.NotBlank;

public record MemberGetByNicknameRequest(
        String nickname,
        Integer friendsOrder,
        Long next,
        Integer size
) {
}
