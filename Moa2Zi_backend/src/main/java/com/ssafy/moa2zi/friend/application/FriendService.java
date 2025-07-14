package com.ssafy.moa2zi.friend.application;

import com.ssafy.moa2zi.friend.domain.Friend;
import com.ssafy.moa2zi.friend.domain.FriendRepository;
import com.ssafy.moa2zi.friend.domain.Status;
import com.ssafy.moa2zi.friend.dto.request.FriendSearchRequest;
import com.ssafy.moa2zi.friend.dto.response.FriendSearchResponse;
import com.ssafy.moa2zi.member.domain.Member;
import com.ssafy.moa2zi.member.domain.MemberRepository;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.notification.application.NotificationService;
import com.ssafy.moa2zi.notification.domain.NotificationMessage;
import com.ssafy.moa2zi.notification.application.NotificationProducer;
import com.ssafy.moa2zi.notification.dto.SenderInfo;
import com.ssafy.moa2zi.notification.domain.NotificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

@Service
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;
    private final NotificationProducer notificationProducer;

    @Transactional
    public void requestFriend (
            Long acceptId,
            CustomMemberDetails loginMember
    ) throws Exception {

        Member requestMember = findMemberById(loginMember.getMemberId());
        Member acceptMember = findMemberById(acceptId);
        validateRequestPermission(requestMember, acceptMember);
        isAlreadyFriendRequest(requestMember, acceptMember);

        Friend friend = Friend.createFriend(requestMember, acceptMember);
        friendRepository.save(friend);

        notificationProducer.send(NotificationMessage.builder()
                        .senderId(requestMember.getMemberId())
                        .receiverId(acceptId)
                        .notificationType(NotificationType.FRIEND_REQUEST)
                        .build());
    }

    private void validateRequestPermission(Member requestMember, Member acceptMember) throws IllegalAccessException {
        if(requestMember.getMemberId().equals(acceptMember.getMemberId())) {
            throw new IllegalAccessException("자신에게 친구 요청을 할 수 없습니다.");
        }
    }

    private void isAlreadyFriendRequest(Member requestMember, Member acceptMember) throws IllegalAccessException {
        if(friendRepository.existsByRequestIdAndAcceptId(requestMember.getMemberId(), acceptMember.getMemberId())
            || friendRepository.existsByRequestIdAndAcceptId(acceptMember.getMemberId(), requestMember.getMemberId())) {
            throw new IllegalAccessException("이미 친구 요청 이력이 존재합니다.");
        }
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new NotFoundException("findMemberById, memebrId = " + memberId));
    }

    @Transactional
    public void acceptFriend(
            Long friendId,
            CustomMemberDetails loginMember
    ) throws Exception {

        Member member = findMemberById(loginMember.getMemberId());
        Friend friend = findFriendById(friendId);
        validateAcceptPermission(member, friend);
        isAlreadyAccepted(friend);
        friend.markAsAccepted();

        notificationProducer.send(NotificationMessage.builder()
                        .senderId(member.getMemberId())
                        .receiverId(friend.getRequestId())
                        .notificationType(NotificationType.FRIEND_ACCEPT)
                        .build());
    }

    private void isAlreadyAccepted(Friend friend) throws IllegalAccessException {
        if(friend.getStatus().equals(Status.ACCEPTED)){
            throw new IllegalAccessException("이미 수락된 상태입니다.");
        }
    }

    private void validateAcceptPermission(Member member, Friend friend) throws IllegalAccessException {
        if(!friend.getAcceptId().equals(member.getMemberId())){
            throw new IllegalAccessException("수락할 수 있는 권한이 없습니다.");
        }
    }

    private Friend findFriendById(Long friendId) {
        return friendRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("findFriendById, friendId = " + friendId));
    }

    @Transactional
    public void deleteFriend(
            Long friendId,
            CustomMemberDetails loginMember
    ) throws Exception {

        Member member = findMemberById(loginMember.getMemberId());
        Friend friend = findFriendById(friendId);
        validateDeletePermission(member, friend);
        friendRepository.delete(friend);

        // 내가 요청 보낸 친구 관계 인지 확인하여 알림을 보낼 상대 지정
        Long receiverId = (member.getMemberId().equals(friend.getAcceptId())) ? friend.getRequestId() : friend.getAcceptId();
        notificationProducer.send(NotificationMessage.builder()
                .senderId(loginMember.getMemberId())
                .receiverId(receiverId)
                .notificationType(NotificationType.FRIEND_REJECT)
                .build());
    }

    private void validateDeletePermission(Member member, Friend friend) throws IllegalAccessException {
        if(!friend.getAcceptId().equals(member.getMemberId()) && !friend.getRequestId().equals(member.getMemberId())) {
            throw new IllegalAccessException("친구 관계를 취소할 수 있는 권한이 없습니다.");
        }
    }

    public FriendSearchResponse getFriends(FriendSearchRequest request) {
        return friendRepository.getFriends(request);
    }

}
