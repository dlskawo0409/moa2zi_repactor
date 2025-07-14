package com.ssafy.moa2zi.game.domain;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.game.dto.response.GameGetResponse;
import com.ssafy.moa2zi.game.dto.response.GameScoreRanking;
import com.ssafy.moa2zi.game.dto.response.GameScoreSummary;
import com.ssafy.moa2zi.game.dto.response.GameWithQuizId;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ssafy.moa2zi.game.domain.QGame.game;
import static com.ssafy.moa2zi.lounge_participant.domain.QLoungeParticipant.loungeParticipant;
import static com.ssafy.moa2zi.quiz.domain.QQuiz.quiz;
import static com.ssafy.moa2zi.answer.domain.QAnswer.answer;
import static com.ssafy.moa2zi.lounge.domain.QLounge.lounge;



@Slf4j
@Repository
@RequiredArgsConstructor
public class GameRepositoryCustomImpl implements GameRepositoryCustom {

    private final EntityManager entityManager;
    @Value("${app.batch-size}") private int batchSize;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GameGetResponse> getGameListWith(Long loungeId, Long memberId){

        List<GameWithQuizId> gameWithQuizIdList = queryFactory
                .select(
                        Projections.constructor(
                                GameWithQuizId.class,
                                game.id,
                                game.createdAt,
                                game.endTime,
                                quiz.id
                        )
                )
                .from(game)
                .leftJoin(quiz).on(quiz.gameId.eq(game.id))
                .where(
                        game.loungeId.eq(loungeId)
                )
                .fetch();

        Map<Long, List<GameWithQuizId>> groupedByGameId = gameWithQuizIdList.stream()
                .collect(Collectors.groupingBy(GameWithQuizId::gameId));

        LocalDateTime today = LocalDateTime.now();

        Long totalMember = queryFactory
                .select(loungeParticipant.count())
                .from(loungeParticipant)
                .where(loungeParticipant.loungeId.eq(loungeId))
                .fetchOne();

//        // 각 gameId에 대해 GameGetResponse 만들기
        return groupedByGameId.entrySet().stream()
                // key 는 gameId 이므로, 내림차순으로 정렬
                .sorted((e1, e2) -> e2.getKey().compareTo(e1.getKey()))
                .map(entry -> {
                    Long gameId = entry.getKey();
                    List<GameWithQuizId> tempGameList = entry.getValue();

                    LocalDateTime createdAt = tempGameList.get(0).createdAt();
                    LocalDateTime endTime = tempGameList.get(0).endTime();
                    List<Long> quizIdList = tempGameList.stream()
                            .map(GameWithQuizId::quizId)
                            .toList();

                    GameStatus gameStatus = endTime.isAfter(today) ? GameStatus.RUNNING : GameStatus.COMPLETED;

                    // quizIdList 가 없거나 null 이 포함되어 있다면
                    if (quizIdList.isEmpty() || quizIdList.contains(null)) {
                        return GameGetResponse.builder()
                                .gameId(gameId)
                                .loungeId(loungeId)
                                .gameStatus(gameStatus)
                                .totalMember(totalMember)
                                .solvedMember(0L)
                                .createdAt(createdAt)
                                .endTime(endTime)
                                .nextQuizId(-1L)
                                .build();
                    }

                    // solvedMember 구하기
                    List<Long> solvedMemberList = queryFactory
                            .select(answer.memberId.count()) // 여기서 'count()'가 아닌 'answer.memberId' 만 가져온 뒤 size()를 세는 등으로 수정 가능
                            .from(quiz)
                            .leftJoin(answer).on(answer.quizId.eq(quiz.id))
                            .where(quiz.id.in(quizIdList))
                            .groupBy(answer.memberId)
                            .having(answer.count().eq((long) quizIdList.size()))
                            .fetch();

                    Long solvedMember = (long) solvedMemberList.size();

                    // 특정 member 의 마지막 퀴즈 ID 조회
                    Long lastQuizId = queryFactory
                            .select(answer.quizId.max())
                            .from(answer)
                            .leftJoin(quiz).on(answer.quizId.eq(quiz.id))
                            .leftJoin(game).on(quiz.gameId.eq(game.id))
                            .where(
                                    answer.memberId.eq(memberId),
                                    game.id.eq(gameId)
                            )
                            .fetchOne();

                    if (lastQuizId == null) {
                        lastQuizId = -1L;
                    }

                    // nextQuizId 조회
                    Long nextQuizId = queryFactory
                            .select(quiz.id)
                            .from(quiz)
                            .where(
                                    quiz.gameId.eq(gameId),
                                    quiz.id.gt(lastQuizId)
                            )
                            .orderBy(quiz.id.asc())
                            .limit(1)
                            .fetchOne();

                    return GameGetResponse.builder()
                            .gameId(gameId)
                            .loungeId(loungeId)
                            .gameStatus(gameStatus)
                            .totalMember(totalMember)
                            .solvedMember(solvedMember)
                            .createdAt(createdAt)
                            .endTime(endTime)
                            .nextQuizId(nextQuizId)
                            .build();
                })
                .toList();
    }

    @Override
    public List<GameScoreRanking> getGameHistory(CustomMemberDetails loginMember){
        List<GameScoreSummary> gameScoreSummaryList = queryFactory.select(
                    Projections.constructor(
                            GameScoreSummary.class,
                            lounge.id,
                            lounge.title,
                            game.id,
                            answer.memberId,
                            new CaseBuilder()
                                    .when(answer.submission.eq(quiz.answer)).then(1)
                                    .otherwise(0).sum(), // 정답 수
                            game.endTime,
                            answer.id.max()
                    )
                )
                .from(quiz)
                .join(answer).on(quiz.id.eq(answer.quizId))
                .join(game).on(quiz.gameId.eq(game.id))
                .leftJoin(lounge).on(lounge.id.eq(game.loungeId))
                .groupBy(quiz.gameId,answer.memberId)
                .fetch();

        Map<Long, List<GameScoreSummary>> groupedByGame =
                gameScoreSummaryList.stream()
                        .collect(Collectors.groupingBy(GameScoreSummary::gameId));

        List<GameScoreRanking> result = new ArrayList<>();

        for (Map.Entry<Long, List<GameScoreSummary>> entry : groupedByGame.entrySet()) {
            Long gameId = entry.getKey();
            List<GameScoreSummary> summaries = entry.getValue();

            // 정답 수 내림차순 정렬
            summaries.sort(
                    Comparator.comparing(GameScoreSummary::correct).reversed()
                            .thenComparing(GameScoreSummary::maxAnswerId) // answer.id.max() 값 기준 오름차순
            );


            int rank = 1;
            int index = 1;
            Integer previousCorrect = null;

            for (GameScoreSummary summary : summaries) {
                if (previousCorrect != null) {
                    rank = index;
                }

                if (summary.memberId().equals(loginMember.getMemberId())) {

                    Long count = queryFactory.select(loungeParticipant.count())
                                    .from(loungeParticipant)
                                            .where(loungeParticipant.loungeId.eq(summary.loungeId()))
                                                    .fetchOne();


                    result.add(new GameScoreRanking(
                            summary.loungeId(),
                            summary.loungeName(),
                            summary.gameId(),
                            summary.memberId(),
                            summary.endTime(),
                            rank,
                            count
                    ));
                }

                previousCorrect = summary.correct();
                index++;
            }
        }

        result.sort(Comparator.comparing(GameScoreRanking::endTime).reversed());

        return result;

    }


}

