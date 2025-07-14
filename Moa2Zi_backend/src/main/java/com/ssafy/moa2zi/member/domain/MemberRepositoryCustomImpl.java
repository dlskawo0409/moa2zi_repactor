package com.ssafy.moa2zi.member.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.friend.domain.Status;
import com.ssafy.moa2zi.member.dto.request.MemberGetByNicknameRequest;
import com.ssafy.moa2zi.member.dto.response.MemberGetByNicknameListResponse;
import com.ssafy.moa2zi.member.dto.response.MemberGetByNicknameResponse;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ssafy.moa2zi.member.domain.QMember.member;
import static com.ssafy.moa2zi.friend.domain.QFriend.friend;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl  implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> getMemberIdListByNickname(String nickname, Long memberId) {
        return queryFactory
                .select(member.memberId)
                .from(member)
                .where(
                        member.nickname.lower().like("%" + nickname.toLowerCase() + "%"),
                        member.memberId.ne(memberId)
                )
                .fetch();
    }

    @Override
    public MemberGetByNicknameListResponse getMemberByNickname(
            MemberGetByNicknameRequest memberGetByNicknameRequest,
            Long memberId
    ) throws BadRequestException {

        int size = memberGetByNicknameRequest.size() == null || memberGetByNicknameRequest.size() <= 0
                ? 10 : memberGetByNicknameRequest.size();

        // friends_order 정의
        NumberExpression<Integer> friendsOrderExpr = new CaseBuilder()
                .when((friend.acceptId.eq(memberId).or(friend.requestId.eq(memberId)))
                        .and(friend.status.eq(Status.ACCEPTED))).then(0)
                .when((friend.acceptId.eq(memberId).or(friend.requestId.eq(memberId)))
                        .and(friend.status.eq(Status.PENDING))).then(1)
                .otherwise(2);

        // member + friends left join 결과 중, friend 관계가 가장 가까운 것 하나만 집계
        List<MemberGetByNicknameResponse> memberList = queryFactory
                .select(
                        Projections.constructor(
                                MemberGetByNicknameResponse.class,
                                friendsOrderExpr.min(),  // MIN으로 집계
                                member.memberId,
                                member.nickname,
                                member.profileImage
                        )
                )
                .from(member)
                .leftJoin(friend)
                .on(friend.acceptId.eq(member.memberId)
                        .or(friend.requestId.eq(member.memberId)))
                .where(
                        member.memberId.ne(memberId),
                        nicknameContainsCond(memberGetByNicknameRequest.nickname()),
                        cursorByFriendsOrderAndNext(
                                memberGetByNicknameRequest.friendsOrder(),
                                memberGetByNicknameRequest.next(),
                                friendsOrderExpr
                        )
                )
                .groupBy(member.memberId, member.nickname, member.profileImage)
                .orderBy(friendsOrderExpr.min().asc(), member.memberId.asc())
                .limit(size + 1)
                .fetch();

        Long total = queryFactory
                .select(member.memberId.countDistinct())
                .from(member)
                .leftJoin(friend)
                .on(friend.acceptId.eq(member.memberId)
                        .or(friend.requestId.eq(member.memberId)))
                .where(
                        member.memberId.ne(memberId),
                        nicknameContainsCond(memberGetByNicknameRequest.nickname())
                )
                .fetchOne();

        size = Math.min(size, memberList.size());
        boolean hasNext = memberList.size() > size;

        Long next = hasNext ? memberList.get(size - 1).memberId() : null;
        Integer friendsOrder = hasNext ? memberList.get(size - 1).friendsOrder() : null;

        if (hasNext) {
            memberList.remove(size);
        }

        return MemberGetByNicknameListResponse.builder()
                .memberList(memberList)
                .hasNext(hasNext)
                .total(total)
                .friendsOrder(friendsOrder)
                .next(next)
                .size(size)
                .build();
    }


    private BooleanExpression nicknameContainsCond(String nickname){
        if (nickname == null || nickname.isBlank()) {
            return null;
        }
        return member.nickname.contains(nickname);
    }

    private BooleanExpression cursorByFriendsOrderAndNext(
            Integer friendOrder,
            Long next,
            NumberExpression<Integer> caseExpression

    ){
        BooleanExpression condition = null;

        if(friendOrder != null && next != null){
            condition = caseExpression.gt(friendOrder)
                    .or(caseExpression.eq(friendOrder).and(member.memberId.gt(next)));

        }

        return condition;

    }


}
