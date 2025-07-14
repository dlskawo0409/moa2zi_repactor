package com.ssafy.moa2zi.quiz.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithRankResultResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithRankingResponse;
import com.ssafy.moa2zi.quiz.dto.response.QuizWithResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static com.ssafy.moa2zi.quiz.domain.QQuiz.quiz;
import static com.ssafy.moa2zi.game.domain.QGame.game;
import static com.ssafy.moa2zi.answer.domain.QAnswer.answer;
import static com.ssafy.moa2zi.member.domain.QMember.member;
import static com.ssafy.moa2zi.lounge_participant.domain.QLoungeParticipant.loungeParticipant;

@Repository
@RequiredArgsConstructor
public class QuizRepositoryCustomImpl implements QuizRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuizWithResultResponse> getQuizWithResult(
            Long gameId,
            Long memberId
    ){

        return queryFactory
                .select(
                        Projections.constructor(
                                QuizWithResultResponse.class,
                                quiz.id,
                                quiz.context,
                                new CaseBuilder()
                                        .when(quiz.answer.eq(answer.submission)).then(true)
                                        .otherwise(false),
                                answer.submission,
                                quiz.memberId
                        )
                )
                .from(quiz)
                .leftJoin(game).on(quiz.gameId.eq(game.id))
                .leftJoin(answer).on(quiz.id.eq(answer.quizId))
                .where(
                    game.id.eq(gameId),
                    answer.memberId.eq(memberId)
                )
                .fetch();

    }


    @Override
    public QuizWithRankResultResponse getQuizWithRanking(
            Long gameId,
            Long memberId,
            List<Long> participantIdList
    ) {
        NumberExpression<Integer> correctCountExpr = new CaseBuilder()
                .when(quiz.answer.eq(answer.submission)).then(1)
                .otherwise(0)
                .sum();

        NumberExpression<Long> maxAnswerId = answer.id.max();

        List<QuizWithRankingResponse> result = queryFactory
                .select(
                        Projections.constructor(
                                QuizWithRankingResponse.class,
                                member.memberId,
                                member.nickname,
                                correctCountExpr
                        )
                )
                .from(member)
                .leftJoin(answer).on(answer.memberId.eq(member.memberId))
                .leftJoin(quiz).on(quiz.id.eq(answer.quizId))
                .leftJoin(game).on(quiz.gameId.eq(game.id))
                .where(
                        member.memberId.in(participantIdList),
                        game.id.eq(gameId).or(game.id.isNull()) // 핵심 포인트!
                )
                .groupBy(member.memberId)
                .orderBy(
                        correctCountExpr.desc(),
                        maxAnswerId.asc()
                )
                .fetch();

        int myRanking = IntStream.range(0, result.size())
                .filter(i -> Objects.equals(result.get(i).memberId(), memberId))
                .map(i -> i + 1)
                .findFirst()
                .orElse(-1);

        Long totalQuizCount = queryFactory
                .select(quiz.count())
                .from(quiz)
                .where(
                        quiz.gameId.eq(gameId)
                )
                .fetchOne();

        return QuizWithRankResultResponse.builder()
                .totalMemberCount(result.size())
                .myRanking(myRanking)
                .totalQuizCount(totalQuizCount)
                .quizWithRankingResponseList(result)
                .build();
    }


}
