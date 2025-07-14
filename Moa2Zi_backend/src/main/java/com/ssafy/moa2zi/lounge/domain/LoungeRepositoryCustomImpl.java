package com.ssafy.moa2zi.lounge.domain;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.game.domain.QGame;
import com.ssafy.moa2zi.lounge.dto.request.LoungeGetRequest;
import com.ssafy.moa2zi.lounge.dto.response.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;


import static com.ssafy.moa2zi.lounge.domain.QLounge.lounge;
import static com.ssafy.moa2zi.member.domain.QMember.member;
import static com.ssafy.moa2zi.lounge_participant.domain.QLoungeParticipant.loungeParticipant;
import static com.ssafy.moa2zi.game.domain.QGame.game;

@RequiredArgsConstructor
public class LoungeRepositoryCustomImpl implements LoungeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<LoungeWithGame> getLoungeWithGame(
            int size,
            LoungeGetRequest loungeGetRequest,
            LocalDateTime now,
            Long memberId
    ) {

        QGame gameSub = new QGame("gameSub");

        // 1) 진행 중(0) / 종료(1) 여부를 계산하는 Case 식
        NumberExpression<Integer> isRunningCase = new CaseBuilder()
                .when(game.endTime.gt(now)).then(0)
                .otherwise(1);

        // 2) 커서 조건: "(isRunning < cursorIsRunning) or (isRunning = cursorIsRunning and lounge.id < cursorId)"
        //    - isRunning은 ASC, lounge.id는 DESC 이므로,
        //      "lounge.id < cursorId" 쪽 비교도 주의해야 합니다(“현재 페이지의 마지막 id보다 작아야 다음 페이지”)
        BooleanBuilder whereForKeyset = new BooleanBuilder();
        if (loungeGetRequest.next() != null && loungeGetRequest.loungeStatus() != null) {
            int cursor = loungeGetRequest.loungeStatus() == LoungeStatus.RUNNING ? 0 : 1;

            whereForKeyset.and(
                    isRunningCase.gt(cursor)
                            .or(
                                    isRunningCase.eq(cursor)
                                            .and(lounge.id.lt(loungeGetRequest.next())
                                    )
                            )
            ).and(loungeParticipant.memberId.eq(memberId));
        }

        // 3) 메인 쿼리
        List<LoungeWithGame> result = queryFactory
                .select(
                        Projections.constructor(
                                LoungeWithGame.class,
                                lounge.id,
                                lounge.title,
                                lounge.createdAt,
                                lounge.endTime,
                                lounge.duration,
                                game.endTime
                        )
                )
                .from(lounge)
                .leftJoin(loungeParticipant).on(lounge.id.eq(loungeParticipant.loungeId))
                .leftJoin(game).on(
                        game.loungeId.eq(lounge.id)
                                .and(game.createdAt.eq(
                                        JPAExpressions
                                                .select(gameSub.createdAt.max())
                                                .from(gameSub)
                                                .where(gameSub.loungeId.eq(lounge.id))
                                ))
                )
        .where(
                whereForKeyset,
                loungeParticipant.memberId.eq(memberId),
                containKeyword(loungeGetRequest.keyword())
        )
                .orderBy(
                        isRunningCase.asc(),
                        lounge.id.desc()
                )
                .limit(size + 1)
                .fetch();

        return result;
    }


    @Override
    public List<LoungeWithParticipant> getLoungeWithParticipantByLoungeIdList(
            List<Long> loungeIdList
    ){

        return queryFactory
            .select(
                Projections.constructor(
                    LoungeWithParticipant.class,
                    loungeParticipant.loungeId,
                    loungeParticipant.memberId,
                    member.nickname,
                    member.profileImage
                )
            )
            .from(loungeParticipant)
            .leftJoin(member).on(loungeParticipant.memberId.eq(member.memberId))
            .where(
                    loungeParticipant.loungeId.in(loungeIdList)
            )
            .orderBy(loungeParticipant.loungeId.desc(), member.memberId.asc()) // 정렬 조건 추가 가능
            .fetch();
    }


    private BooleanExpression loungeAfterTime(LocalDateTime dateTime) {
        return (dateTime != null)
                ? lounge.endTime.goe(dateTime)
                : null;
    }


    @Override
    public Long getTotal(
        LoungeGetRequest loungeGetRequest,
        Long memberId
    ){
        return queryFactory
            .select(lounge.count())
            .from(lounge)
            .leftJoin(loungeParticipant).on(lounge.id.eq(loungeParticipant.loungeId))
            .where(
                loungeParticipant.memberId.eq(memberId),
                containKeyword(loungeGetRequest.keyword())
            )
            .fetchOne();
    }


    private OrderSpecifier<Integer> isRunningGame(LocalDateTime now) {
        return new CaseBuilder()
                .when(game.endTime.isNotNull().and(game.endTime.gt(now))).then(0)
                .otherwise(1)
                .asc();
    }
    private BooleanExpression geNextId(Long next) {
        return next == null ? null : lounge.id.loe(next);
    }



    private BooleanExpression containKeyword(String keyword) {
        return (keyword != null && !keyword.isBlank())
                ? lounge.title.containsIgnoreCase(keyword)
                : Expressions.TRUE;
    }

    @Override
    public List<Lounge> getLoungeListLargeThanEndTime(LocalDateTime dateTime){
        return queryFactory
                .select(lounge)
                .from(lounge)
                .where(
                        lounge.endTime.goe(dateTime)
                )
                .fetch();
    }


    @Override
    public List<Long> getLoungeIdListByMemberId(Long memberId){
        return queryFactory
                .select(lounge.id)
                .from(lounge)
                .leftJoin(loungeParticipant).on(lounge.id.eq(loungeParticipant.loungeId))
                .where(
                        loungeParticipant.memberId.eq(memberId)
                )
                .fetch();

    }

    @Override
    public List<LoungeWithGameAndParticipant> getLoungeWithGameAndParticipantListByLoungeIdAndMemberId(
            LoungeGetRequest loungeGetRequestWithNickname,
            List<Long> loungeIdList,
            List<Long> memberIdList,
            Long memberId
    ){
        int size = loungeGetRequestWithNickname.size() == null ? 10 : loungeGetRequestWithNickname.size();
        QGame gameSub = new QGame("gameSub");
        LocalDateTime today = LocalDateTime.now();


        NumberExpression<Integer> isRunningCase = new CaseBuilder()
                .when(game.endTime.gt(today)).then(0)
                .otherwise(1);

        // 2) 커서 조건: "(isRunning < cursorIsRunning) or (isRunning = cursorIsRunning and lounge.id < cursorId)"
        //    - isRunning은 ASC, lounge.id는 DESC 이므로,
        //      "lounge.id < cursorId" 쪽 비교도 주의해야 합니다(“현재 페이지의 마지막 id보다 작아야 다음 페이지”)
        BooleanBuilder whereForKeyset = new BooleanBuilder();
        if (loungeGetRequestWithNickname.next() != null && loungeGetRequestWithNickname.loungeStatus() != null) {
            int cursor = loungeGetRequestWithNickname.loungeStatus() == LoungeStatus.RUNNING ? 0 : 1;

            whereForKeyset.and(
                    isRunningCase.gt(cursor)
                            .or(
                                    isRunningCase.eq(cursor)
                                            .and(lounge.id.lt(loungeGetRequestWithNickname.next())
                                            )
                            )
            ).and(loungeParticipant.memberId.eq(memberId));
        }




        return queryFactory
                .select(
                        Projections.constructor(
                                LoungeWithGameAndParticipant.class,
                                lounge.id,
                                lounge.title,
                                lounge.createdAt,
                                lounge.endTime,
                                game.endTime,
                                loungeParticipant.memberId,
                                member.nickname,
                                member.profileImage
                        )
                )
                .from(lounge)
                .leftJoin(loungeParticipant).on(lounge.id.eq(loungeParticipant.loungeId))
                .leftJoin(member).on(member.memberId.eq(loungeParticipant.memberId))
                .leftJoin(game).on(
                        game.loungeId.eq(lounge.id)
                                .and(game.createdAt.eq(
                                        JPAExpressions
                                                .select(gameSub.createdAt.max())
                                                .from(gameSub)
                                                .where(gameSub.loungeId.eq(lounge.id))
                                ))
                )
                .where(
                        whereForKeyset,
                        lounge.id.in(loungeIdList),
                        JPAExpressions.selectOne()
                                .from(loungeParticipant)
                                .where(
                                        loungeParticipant.loungeId.eq(lounge.id),
                                        loungeParticipant.memberId.in(memberIdList)
                                )
                                .exists()
                )

                .orderBy(
                        isRunningGame(today),
                        lounge.id.desc()
                )
                .limit(size + 1)
                .fetch();
    }

    @Override
    public Long getLoungeWithNicknameTotal(
            List<Long> loungeIdList,
            List<Long> memberIdList
    ){
        return queryFactory
                .select(lounge.id.countDistinct())
                .from(lounge)
                .leftJoin(loungeParticipant).on(lounge.id.eq(loungeParticipant.loungeId))
                .leftJoin(member).on(member.memberId.eq(loungeParticipant.memberId))
                .where(
                        lounge.id.in(loungeIdList),
                        loungeParticipant.memberId.in(memberIdList)
                )
                .fetchOne();
    }

    @Override
    public Long getLoungeIdByGameId(Long gameId) {
        return Optional.ofNullable(
                queryFactory
                        .select(lounge.id)
                        .from(lounge)
                        .join(game).on(lounge.id.eq(game.loungeId))
                        .where(game.id.eq(gameId))
                        .fetchFirst()
        ).orElseThrow(() -> new NoSuchElementException("해당 gameId에 대한 lounge가 존재하지 않습니다."));
    }


    @Override
    public Optional<LoungeWithGame> getLoungeByGameId(Long gameId){

        return Optional.ofNullable(queryFactory.
                select(
                        Projections.constructor(
                                LoungeWithGame.class,
                                lounge.id,
                                lounge.title,
                                lounge.createdAt,
                                lounge.endTime,
                                lounge.duration,
                                game.endTime
                        )
                )
                .from(game)
                .leftJoin(lounge).on(game.loungeId.eq(lounge.id))
                .where(
                        game.id.eq(gameId)
                )
                .fetchOne());

    }
}
