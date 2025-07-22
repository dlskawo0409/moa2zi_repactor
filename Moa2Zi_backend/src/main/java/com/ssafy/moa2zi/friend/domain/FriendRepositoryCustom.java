package com.ssafy.moa2zi.friend.domain;

import com.ssafy.moa2zi.friend.dto.request.FriendSearchRequest;
import com.ssafy.moa2zi.friend.dto.response.FriendSearchResponse;

public interface FriendRepositoryCustom {
    FriendSearchResponse getFriends(FriendSearchRequest request);
    boolean areTheyFriend(Long memberId1, Long memberId2);
}
