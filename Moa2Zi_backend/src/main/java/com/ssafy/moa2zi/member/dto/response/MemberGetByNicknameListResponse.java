package com.ssafy.moa2zi.member.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record MemberGetByNicknameListResponse(
        List<MemberGetByNicknameResponse> memberList,
        Long total,
        Integer friendsOrder,
        Boolean hasNext,
        Long next,
        Integer size
) {
}
