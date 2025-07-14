package com.ssafy.moa2zi.friend.dto.request;

import com.ssafy.moa2zi.friend.domain.Status;

public record FriendSearchRequest(
        Long requestId,
        Long acceptId,
        Status status,
        Long next,
        Integer size
) {
    public FriendSearchRequest {
        if (size == null || size == 0) {
            size = 10;
        }
    }
}
