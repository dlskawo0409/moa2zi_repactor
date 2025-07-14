package com.ssafy.moa2zi.friend.dto.response;

import com.ssafy.moa2zi.member.dto.response.MemberGetResponse;
import com.ssafy.moa2zi.member.dto.response.MemberInfoResponse;

import java.time.LocalDateTime;

public record FriendInfoResponse(
        Long friendId,
        MemberInfoResponse requestMember,
        MemberInfoResponse acceptMember,
        LocalDateTime updatedAt
) {
}
