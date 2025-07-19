package com.ssafy.moa2zi.friend.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record FriendSearchResponse(
    List<FriendInfoResponse> friendList,
    Long total,
    int size,
    boolean hasNext,
    Long next
) {
}
