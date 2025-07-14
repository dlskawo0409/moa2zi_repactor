package com.ssafy.moa2zi.lounge_participant.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.lounge.dto.response.LoungeWithParticipant;
import com.ssafy.moa2zi.lounge_participant.dto.response.LoungeParticipantGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.ssafy.moa2zi.lounge_participant.domain.QLoungeParticipant.loungeParticipant;
import static com.ssafy.moa2zi.member.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class LoungeParticipantRepositoryCustomImpl implements LoungeParticipantRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean existsByLoungeIdAndMemberId(
            Long loungeId,
            Long memberId
    )
    {
        Long count = queryFactory
                .select(loungeParticipant.count())
                .from(loungeParticipant)
                .where(
                        loungeParticipant.loungeId.eq(loungeId),
                        loungeParticipant.memberId.eq(memberId)
                )
                .fetchOne();

        return count != null && count >= 1;

    }

    @Override
    public List<LoungeParticipantGetResponse> getLoungeParticipantWithLoungeIdAndMemberId(Long loungeId){
        return queryFactory
                .select(
                        Projections.constructor(
                                LoungeParticipantGetResponse.class,
                                member.memberId,
                                member.nickname,
                                member.profileImage
                        )
                )
                .from(loungeParticipant)
                .leftJoin(member).on(loungeParticipant.memberId.eq(member.memberId))
                .where(
                        loungeParticipant.loungeId.eq(loungeId)
                )
                .orderBy(member.memberId.asc())
                .fetch();
    }
}
