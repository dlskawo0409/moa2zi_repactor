package com.ssafy.moa2zi.friend.presentation;

import com.ssafy.moa2zi.friend.application.FriendService;
import com.ssafy.moa2zi.friend.dto.request.FriendSearchRequest;
import com.ssafy.moa2zi.friend.dto.response.FriendSearchResponse;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/friends")
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/{acceptId}")
    public ResponseEntity<Void> requestFriend(
            @PathVariable(name = "acceptId") Long acceptId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        friendService.requestFriend(acceptId, loginMember);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{friendId}")
    public ResponseEntity<Void> acceptFriend(
            @PathVariable(name = "friendId") Long friendId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        friendService.acceptFriend(friendId, loginMember);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(
            @PathVariable(name = "friendId") Long friendId,
            @AuthenticationPrincipal CustomMemberDetails loginMember
    ) throws Exception {

        friendService.deleteFriend(friendId, loginMember);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<FriendSearchResponse> getFriends(
            FriendSearchRequest request
    ) throws Exception {

        FriendSearchResponse result = friendService.getFriends(request);
        return ResponseEntity.ok(result);
    }

}
