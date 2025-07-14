package com.ssafy.moa2zi.day.domain;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.day.dto.request.DayCommentSearchRequest;
import com.ssafy.moa2zi.day.dto.response.DayCommentInfoResponse;
import com.ssafy.moa2zi.day.dto.response.DayCommentSearchResponse;
import com.ssafy.moa2zi.member.domain.QMember;
import com.ssafy.moa2zi.member.dto.response.MemberInfoResponse;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import static com.ssafy.moa2zi.day.domain.QDay.day;
import static com.ssafy.moa2zi.day.domain.QDayComment.dayComment;
import static com.ssafy.moa2zi.member.domain.QMember.member;

@RequiredArgsConstructor
public class DayCommentRepositoryCustomImpl implements DayCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public DayCommentSearchResponse findComments(
            Long dayId,
            DayCommentSearchRequest request
    ) {
        QDayComment childComment = new QDayComment("childComment");

        List<DayCommentInfoResponse> commentList = queryFactory
                .select(Projections.constructor(
                        DayCommentInfoResponse.class,
                        dayComment.id,
                        Projections.constructor(
                                MemberInfoResponse.class,
                                member.memberId,
                                member.nickname,
                                member.profileImage,
                                member.createdAt
                        ),
                        dayComment.content,
                        dayComment.updatedAt,
                        JPAExpressions
                                .select(childComment.count())
                                .from(childComment)
                                .where(childComment.parentId.eq(dayComment.id))
                ))
                .from(dayComment)
                .leftJoin(day).on(day.id.eq(dayComment.dayId))
                .leftJoin(member).on(member.memberId.eq(dayComment.memberId))
                .where(
                        nextCommentId(request.next()),
                        eqDayId(dayId),
                        eqParentId(request)
                )
                .limit(request.size()+1)
                .fetch();

        Long total = queryFactory
                .select(dayComment.count())
                .from(dayComment)
                .where(
                        eqDayId(dayId),
                        eqParentId(request)
                )
                .fetchOne();

        int size = Math.min(commentList.size(), request.size());
        boolean hasNext = commentList.size() > request.size();
        Long next = (hasNext) ? commentList.get(commentList.size() - 1).commentId() : null;
        List<DayCommentInfoResponse> content = (hasNext) ? commentList.subList(0, request.size()) : commentList;

        return new DayCommentSearchResponse(content, total, size, hasNext, next);
    }

    private BooleanExpression nextCommentId(Long nextId) {
        if(Objects.isNull(nextId)) {
            return null;
        }

        return dayComment.id.goe(nextId);
    }

    private BooleanExpression eqDayId(Long dayId) {
        if(Objects.isNull(dayId)) {
            return null;
        }

        return dayComment.dayId.eq(dayId);
    }

    private BooleanExpression eqParentId(DayCommentSearchRequest request) {
        if(Objects.isNull(request.parentId())) {
            return dayComment.parentId.isNull();
        }

        return dayComment.parentId.eq(request.parentId());
    }

}
