package com.ssafy.moa2zi.friend.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.friend.dto.response.FriendInfoResponse;
import com.ssafy.moa2zi.friend.dto.request.FriendSearchRequest;
import com.ssafy.moa2zi.friend.dto.response.FriendSearchResponse;
import com.ssafy.moa2zi.member.domain.QMember;
import com.ssafy.moa2zi.member.dto.response.MemberGetResponse;
import com.ssafy.moa2zi.member.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import static com.ssafy.moa2zi.friend.domain.QFriend.friend;

@RequiredArgsConstructor
public class FriendRepositoryCustomImpl implements FriendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public FriendSearchResponse getFriends(FriendSearchRequest request) {
        QMember requestMember = new QMember("requestMember");
        QMember acceptMember = new QMember("acceptMember");

        List<FriendInfoResponse> friendList = queryFactory
                .select(
                        Projections.constructor(
                                FriendInfoResponse.class,
                                friend.id,
                                Projections.constructor(
                                        MemberInfoResponse.class,
                                        requestMember.memberId,
                                        requestMember.nickname,
                                        requestMember.profileImage,
                                        requestMember.createdAt
                                ),
                                Projections.constructor(
                                        MemberInfoResponse.class,
                                        acceptMember.memberId,
                                        acceptMember.nickname,
                                        acceptMember.profileImage,
                                        acceptMember.createdAt
                                ),
                                friend.updatedAt
                        )
                )
                .from(friend)
                .join(requestMember).on(friend.requestId.eq(requestMember.memberId))
                .join(acceptMember).on(friend.acceptId.eq(acceptMember.memberId))
                .where(
                        nextId(request.next()),
                        eqRequestOrAcceptMemberCond(request),
                        eqStatus(request.status())
                )
                .orderBy(friend.id.desc())
                .limit(request.size()+1)
                .fetch();

        Long total = queryFactory
                .select(friend.count())
                .from(friend)
                .where(
                        eqRequestOrAcceptMemberCond(request),
                        eqStatus(request.status())
                )
                .fetchOne();

        int size = Math.min(friendList.size(), request.size());
        boolean hasNext = friendList.size() > request.size();
        Long next = (hasNext) ? friendList.get(friendList.size() - 1).friendId() : null;
        List<FriendInfoResponse> content = (hasNext) ? friendList.subList(0, request.size()) : friendList;

        return new FriendSearchResponse(content, total, size, hasNext, next);
    }

    private BooleanExpression nextId(Long friendId) {
        if(Objects.isNull(friendId)) {
            return null;
        }

        return friend.id.loe(friendId);
    }

    private BooleanExpression eqRequestOrAcceptMemberCond(FriendSearchRequest request) {
        BooleanExpression condition = null;
        if(!Objects.isNull(request.requestId())) {
            condition = friend.requestId.eq(request.requestId());
        }

        if(!Objects.isNull(request.acceptId())) {
            condition = (condition != null)
                    ? condition.or(friend.acceptId.eq(request.acceptId()))
                    : friend.acceptId.eq(request.acceptId());;
        }

        return condition;
    }

    private BooleanExpression eqStatus(Status status) {
        BooleanExpression condition = null;
        if(!Objects.isNull(status)) {
            condition = friend.status.eq(status);
        }
        return condition;
    }


    @Override
    public boolean areTheyFriend(Long memberId1, Long memberId2) {

        Long friendCount = queryFactory
                .select(friend.count())
                .from(friend)
                .where(
                        eqMutualFriendshipCond(memberId1, memberId2)
                )
                .fetchOne();

        return friendCount != null && friendCount > 0;
    }

    private BooleanExpression eqMutualFriendshipCond(Long memberId1, Long memberId2) {
        BooleanExpression condition = null;
        if (memberId1 != null && memberId2 != null) {
            condition = (friend.requestId.eq(memberId1).and(friend.acceptId.eq(memberId2)))
                    .or(friend.requestId.eq(memberId2).and(friend.acceptId.eq(memberId1)))
                    .and(eqStatus(Status.ACCEPTED));
        }
        return condition;
    }


}
