package com.ssafy.moa2zi.day.dto.response;

import com.ssafy.moa2zi.member.dto.response.MemberGetResponse;
import com.ssafy.moa2zi.member.dto.response.MemberInfoResponse;

import java.time.LocalDateTime;

public record DayCommentInfoResponse(
        Long commentId,
        MemberInfoResponse member,
        String content,
        LocalDateTime updatedAt,
        Long childCommentCount
) {
}
