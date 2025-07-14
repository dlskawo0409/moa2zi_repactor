package com.ssafy.moa2zi.challenge.domain;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ssafy.moa2zi.challenge.dto.request.*;

import com.ssafy.moa2zi.challenge.dto.response.*;

import com.ssafy.moa2zi.challenge.dto.response.ChallengeInfo;
import com.ssafy.moa2zi.challenge.dto.response.ChallengeInfoResponse;
import com.ssafy.moa2zi.challenge.dto.response.ChallengeSearchResponse;
import com.ssafy.moa2zi.member.domain.Gender;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;
import com.ssafy.moa2zi.transaction.domain.TransactionType;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ssafy.moa2zi.category.domain.QCategory.category;
import static com.ssafy.moa2zi.challenge.domain.QChallenge.challenge;
import static com.ssafy.moa2zi.challenge.domain.QChallengeParticipant.challengeParticipant;
import static com.ssafy.moa2zi.challenge.domain.QChallengeRecommend.challengeRecommend;
import static com.ssafy.moa2zi.challenge.domain.QChallengeReviewLike.challengeReviewLike;
import static com.ssafy.moa2zi.challenge.domain.QChallengeTime.challengeTime;
import static com.ssafy.moa2zi.challenge.domain.Status.SUCCESS;
import static com.ssafy.moa2zi.member.domain.QMember.member;
import static com.ssafy.moa2zi.transaction.domain.QTransaction.transaction;

@RequiredArgsConstructor
public class ChallengeRepositoryCustomImpl implements ChallengeRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public ChallengeSearchResponse findChallengesByMember(
            ChallengeSearchRequest request,
            CustomMemberDetails loginMember
    ) {

        List<ChallengeInfo> challengeList = queryFactory
                .select(
                        Projections.constructor(
                                ChallengeInfo.class,
                                challenge.id,
                                challengeTime.id,
                                challengeParticipant.id,
                                challenge.title,
                                challenge.period,
                                challenge.unit,
                                challenge.challengeType,
                                challenge.amount,
                                challenge.limitCount,
                                challenge.percent,
                                challenge.tags,
                                challenge.categoryName,
                                challengeTime.startTime,
                                challengeTime.endTime,
                                challenge.participantCount,
                                challengeParticipant.status
                        )
                )
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId)) // 해당 챌린지에 존재하는 기간
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        nextCond(request),
                        challengeParticipant.memberId.eq(loginMember.getMemberId()),
                        eqStatus(request.status()),
                        containKeyword(request.keyword()),
                        containTag(request.tag())
                )
                .orderBy(challengeParticipant.id.desc())
                .limit(request.size()+1)
                .fetch();

        Long total = queryFactory
                .select(challengeParticipant.count())
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId)) // 해당 챌린지에 존재하는 기간
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        challengeParticipant.memberId.eq(loginMember.getMemberId()),
                        eqStatus(request.status()),
                        containKeyword(request.keyword()),
                        containTag(request.tag())
                )
                .fetchOne();

        int size = Math.min(challengeList.size(), request.size());
        boolean hasNext = challengeList.size() > request.size();
        Long next = (hasNext) ? challengeList.get(challengeList.size() - 1).getChallengeParticipantId() : null;
        List<ChallengeInfo> content = (hasNext) ? challengeList.subList(0, request.size()) : challengeList;

        return new ChallengeSearchResponse(content.stream().map(ChallengeInfoResponse::from).toList(), total, size, hasNext, next, null);
    }

    @Override
    public ParticipantGetResponse findParticipants(
            Long challengeId,
            ParticipantGetRequest request,
            CustomMemberDetails loginMember
    ) {

        List<ParticipantInfoResponse> participantList = queryFactory
                .select(
                        Projections.constructor(
                                ParticipantInfoResponse.class,
                                challengeParticipant.id,
                                member.memberId,
                                member.nickname,
                                member.profileImage,
                                member.gender,
                                challengeParticipant.status
                        )
                )
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId))
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .join(member).on(member.memberId.eq(challengeParticipant.memberId))
                .where(
                        challenge.id.eq(challengeId),
                        nextParticipantId(request.next()),
                        eqStatus(request.status())
                )
                .orderBy(challengeParticipant.id.desc()) // 반드시 내림차순 (현재 진행 중인 참여자를 위로 올리기 위함)
                .limit(request.size()+1)
                .fetch();

        Long total = queryFactory
                .select(challengeParticipant.count())
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId))
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        challenge.id.eq(challengeId),
                        eqStatus(request.status())
                )
                .fetchOne();

        int size = Math.min(participantList.size(), request.size());
        boolean hasNext = participantList.size() > request.size();
        Long next = (hasNext) ? participantList.get(participantList.size() - 1).challengeParticipantId() : null;
        List<ParticipantInfoResponse> content = (hasNext) ? participantList.subList(0, request.size()) : participantList;

        return new ParticipantGetResponse(content, total, size, hasNext, next);
    }

    @Override
    public ReviewGetResponse findReviews(
            Long challengeId,
            ReviewGetRequest request,
            CustomMemberDetails loginMember
    ) {

        // 리뷰 기준 페이징
        List<ReviewInfo> reviewList = queryFactory
                .select(
                        Projections.constructor(
                                ReviewInfo.class,
                                challengeParticipant.id,
                                challengeParticipant.memberId,
                                challengeParticipant.review,
                                challengeParticipant.reviewedAt
                        )
                )
                .from(challenge)
                .join(challengeTime).on(challengeTime.challengeId.eq(challenge.id))
                .join(challengeParticipant).on(challengeParticipant.challengeTimeId.eq(challengeTime.id))
                .where(
                        challenge.id.eq(challengeId),
                        nextReview(request),
                        challengeParticipant.review.isNotNull(),
                        challengeParticipant.reviewedAt.isNotNull()
                )
                .orderBy(
                        challengeParticipant.reviewedAt.desc(),
                        challengeParticipant.id.desc()
                )
                .limit(request.size() + 1)
                .fetch();

        // 조회된 리뷰 ID
        List<Long> participantIds = reviewList.stream().map(ReviewInfo::challengeParticipantId).toList();

        // 리뷰어 정보 가져오기
        List<ParticipantInfoResponse> participantList = queryFactory
                .select(
                        Projections.constructor(
                                ParticipantInfoResponse.class,
                                challengeParticipant.id,
                                member.memberId,
                                member.nickname,
                                member.profileImage,
                                member.gender,
                                challengeParticipant.status
                        )
                )
                .from(challengeParticipant)
                .join(member).on(challengeParticipant.memberId.eq(member.memberId))
                .where(challengeParticipant.id.in(participantIds))
                .fetch();

        Map<Long, ParticipantInfoResponse> paricipantInfoMap = participantList.stream()
                .collect(Collectors.toMap(
                        ParticipantInfoResponse::challengeParticipantId,
                        Function.identity()
                ));


        // 현재 로그인 유저가 좋아요한 리뷰 아이디 셋
        Set<Long> likedReviewSetByUser = new HashSet<>(queryFactory
                .select(challengeReviewLike.challengeParticipantId)
                .from(challengeReviewLike)
                .where(
                        challengeReviewLike.memberId.eq(loginMember.getMemberId()),
                        challengeReviewLike.challengeParticipantId.in(participantIds)
                )
                .fetch());

        // 각 리뷰의 좋아요 수 구하기
        List<Tuple> likeCountList = queryFactory
                .select(
                        challengeReviewLike.challengeParticipantId,
                        challengeReviewLike.count()
                )
                .from(challengeReviewLike)
                .where(challengeReviewLike.challengeParticipantId.in(participantIds))
                .groupBy(challengeReviewLike.challengeParticipantId)
                .fetch();

        Map<Long, Long> reviewLikeCountMap = likeCountList.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(challengeReviewLike.challengeParticipantId),
                        tuple -> tuple.get(challengeReviewLike.count())
                ));

        // 조회한 결과 조합 dto 생성
        List<ReviewInfoResponse> reviewInfoResponseList = reviewList
                .stream()
                .map(reviewInfo -> ReviewInfoResponse.from(
                        reviewInfo,
                        paricipantInfoMap.get(reviewInfo.challengeParticipantId()),
                        reviewLikeCountMap.getOrDefault(reviewInfo.challengeParticipantId(), 0L),
                        likedReviewSetByUser.contains(reviewInfo.challengeParticipantId())
                ))
                .toList();

        Long total = queryFactory
                .select(challengeParticipant.count())
                .from(challenge)
                .join(challengeTime).on(challengeTime.challengeId.eq(challenge.id))
                .join(challengeParticipant).on(challengeParticipant.challengeTimeId.eq(challengeTime.id))
                .where(
                        challenge.id.eq(challengeId),
                        challengeParticipant.review.isNotNull(),
                        challengeParticipant.reviewedAt.isNotNull()
                )
                .fetchOne();

        int size = Math.min(reviewList.size(), request.size());
        boolean hasNext = reviewList.size() > request.size();
        Long next = (hasNext) ? reviewList.get(reviewList.size() - 1).challengeParticipantId() : null;
        LocalDateTime lastTime = (hasNext) ? reviewList.get(reviewList.size() - 1).reviewedAt() : null;
        List<ReviewInfoResponse> content = (hasNext) ? reviewInfoResponseList.subList(0, request.size()) : reviewInfoResponseList;

        return new ReviewGetResponse(content, total, size, hasNext, next, lastTime);
    }

    private BooleanExpression nextReview(ReviewGetRequest request) {
        if(request.lastTime() == null || request.next() == null || request.next() == 0) {
            return null;
        }

        return challengeParticipant.reviewedAt.loe(request.lastTime())
                .and(challengeParticipant.id.loe(request.next()));
    }

    @Override
    public List<Long> findTopNChallengesByCond(
            Long memberId,
            ChallengeRecommendCond challengeRecommendCond,
            int topN,
            Set<Long> excludeIds
    ) {

        // 유저가 참여 중인 ID 모두 조회 (현재 완료된 건 또 참여 가능하므로 필터링하지 않음)
        List<Long> ongoingChallengeIds = findOngoingChallengeIdsByMemberId(memberId);

        return queryFactory
                .select(challengeTime.id)
                .from(challengeTime)
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .leftJoin(challengeParticipant).on(challengeParticipant.challengeTimeId.eq(challengeTime.id))
                .where(
                        challengeTime.startTime.gt(LocalDateTime.now()), // 오픈된 챌린지
                        inCategoryList(challengeRecommendCond.topSpendingCategoryList()),
                        eqGender(challengeRecommendCond.gender()),
                        betweenAge(challengeRecommendCond.age()),
                        notInExcludeIds(challengeRecommendCond.excludePrevious(), excludeIds),
                        challenge.id.notIn(ongoingChallengeIds)
                )
                .orderBy(challenge.participantCount.desc())
                .limit(topN)
                .fetch();
    }

    /*
      이전 추천 목록은 중복 추천하지 않음, 필터링
     */
    private BooleanExpression notInExcludeIds(Boolean excludePrevious, Set<Long> excludeIds) {
        if(!excludePrevious || Objects.isNull(excludeIds)) {
            return null;
        }

        return challengeTime.id.notIn(excludeIds);
    }

    @Override
    public ChallengeSearchResponse findRecommendChallengesByMember(
            CustomMemberDetails loginMember,
            int topN
    ) {

        List<Long> ongoingChallengeIds = findOngoingChallengeIdsByMemberId(loginMember.getMemberId());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime baseTime = now.getHour() < 3 // 새벽 0~3시 사이면 어제 자 새벽 3시가 기준
                ? now.toLocalDate().minusDays(1).atTime(3, 0)
                : now.toLocalDate().atTime(3, 0);

        List<ChallengeInfo> content = queryFactory
                .select(
                        Projections.fields(
                                ChallengeInfo.class,
                                challenge.id.as("challengeId"),
                                challengeTime.id.as("challengeTimeId"),
                                challenge.title,
                                challenge.period,
                                challenge.unit,
                                challenge.challengeType,
                                challenge.amount,
                                challenge.limitCount,
                                challenge.percent,
                                challenge.tags,
                                challenge.categoryName,
                                challengeTime.startTime,
                                challengeTime.endTime,
                                challenge.participantCount
                        )
                )
                .from(challengeRecommend)
                .join(challengeTime).on(challengeTime.id.eq(challengeRecommend.challengeTimeId))
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        challengeRecommend.memberId.eq(loginMember.getMemberId()),
                        challengeRecommend.createdAt.goe(baseTime),
                        challengeTime.startTime.gt(LocalDateTime.now()),
                        challenge.id.notIn(ongoingChallengeIds)
                )
                .orderBy(challengeRecommend.id.desc())
                .limit(topN)
                .fetch();

        return new ChallengeSearchResponse(
                content.stream().map(ChallengeInfoResponse::from).toList(),
                (long) content.size(),
                content.size(),
                false,
                null,
                null);
    }

    private BooleanExpression betweenAge(Integer age) {
        if(Objects.isNull(age)) {
            return null;
        }

        return challengeParticipant.age.between(age - 10, age + 10);
    }

    private BooleanExpression eqGender(Gender gender) {
        if(Objects.isNull(gender)) {
            return null;
        }

        return challengeParticipant.gender.eq(gender);
    }

    private BooleanExpression inCategoryList(List<String> categoryNames) {
        if(Objects.isNull(categoryNames) || categoryNames.isEmpty()) {
            return null;
        }

        return challenge.categoryName.in(categoryNames);
    }


    private BooleanExpression nextReviewedAtTime(LocalDateTime lastTime) {
        if(Objects.isNull(lastTime))
            return null;

        return challengeParticipant.reviewedAt.loe(lastTime);
    }

    private BooleanExpression nextParticipantId(Long nextId) {
        if(Objects.isNull(nextId) || nextId == 0)
            return null;

        return challengeParticipant.id.loe(nextId);
    }

    private List<Long> findOngoingChallengeIdsByMemberId(Long memberId) {

        return queryFactory
                .select(challenge.id)
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId)) // 해당 챌린지에 존재하는 기간
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        challengeParticipant.memberId.eq(memberId),
                        challengeParticipant.status.eq(Status.ONGOING)
                )
                .fetch();
    }

    @Override
    public ChallengeSearchResponse findChallenges(
            ChallengeSearchRequest request,
            CustomMemberDetails loginMember
    ) {

        // 유저가 참여 중인 ID 모두 조회 (현재 완료된 건 또 참여 가능하므로 필터링하지 않음)
        List<Long> ongoingChallengeIds = findOngoingChallengeIdsByMemberId(loginMember.getMemberId());

        List<ChallengeInfo> challengeList = queryFactory
                .select(
                        Projections.fields(
                                ChallengeInfo.class,
                                challenge.id.as("challengeId"),
                                challengeTime.id.as("challengeTimeId"),
                                challenge.title,
                                challenge.period,
                                challenge.unit,
                                challenge.challengeType,
                                challenge.amount,
                                challenge.limitCount,
                                challenge.percent,
                                challenge.tags,
                                challenge.categoryName,
                                challengeTime.startTime,
                                challengeTime.endTime,
                                challenge.participantCount
                        )
                )
                .from(challenge)
                .join(challengeTime).on(challengeTime.challengeId.eq(challenge.id))
                .where(
                        nextCond(request),
                        challenge.id.notIn(ongoingChallengeIds), // 현재 참여 중인 챌린지는 필터링
                        challengeTime.startTime.gt(LocalDateTime.now()),
                        containKeyword(request.keyword()),
                        containTag(request.tag())
                )
                .orderBy(
                        challengeTime.startTime.asc(),
                        challengeTime.id.desc()
                )
                .limit(request.size()+1)
                .fetch();

        Long total = queryFactory
                .select(challenge.count())
                .from(challenge)
                .join(challengeTime).on(challengeTime.challengeId.eq(challenge.id))
                .where(
                        challenge.id.notIn(ongoingChallengeIds),
                        challengeTime.startTime.gt(LocalDateTime.now()),
                        containKeyword(request.keyword()),
                        containTag(request.tag())
                )
                .fetchOne();

        int size = Math.min(challengeList.size(), request.size());
        boolean hasNext = challengeList.size() > request.size();
        Long next = (hasNext) ? challengeList.get(challengeList.size() - 1).getChallengeTimeId() : null;
        LocalDateTime lastStartTime = (hasNext) ? challengeList.get(challengeList.size() - 1).getStartTime() : null;
        List<ChallengeInfo> content = (hasNext) ? challengeList.subList(0, request.size()) : challengeList;

        return new ChallengeSearchResponse(content.stream().map(ChallengeInfoResponse::from).toList(), total, size, hasNext, next, lastStartTime);
    }

    /**
     * 현재 가장 많이 참여하고 있는 챌린지
     */
    @Override
    public ChallengeSearchResponse findPopularChallenges(int topN) {
        List<ChallengeInfo> challengeList = queryFactory.select(
                        Projections.fields(
                                ChallengeInfo.class,
                                challenge.id.as("challengeId"),
                                challengeTime.id.as("challengeTimeId"),
                                challenge.title,
                                challenge.period,
                                challenge.unit,
                                challenge.challengeType,
                                challenge.amount,
                                challenge.limitCount,
                                challenge.percent,
                                challenge.tags,
                                challenge.categoryName,
                                challengeTime.startTime,
                                challengeTime.endTime,
                                challenge.participantCount
                        )
                )
                .from(challenge)
                .join(challengeTime).on(challengeTime.challengeId.eq(challenge.id))
                .where(
                        challengeTime.startTime.gt(LocalDateTime.now())
                )
                .orderBy(challenge.participantCount.desc())
                .limit(topN)
                .fetch();

        int size = Math.min(challengeList.size(), topN);
        boolean hasNext = false;

        return new ChallengeSearchResponse(challengeList.stream().map(ChallengeInfoResponse::from).toList(), Long.valueOf(size), size, hasNext, null, null);
    }

    @Override
    public boolean existsOngoingByMemberIdAndChallengeId(Long memberId, Long challengeId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId))
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        challengeParticipant.memberId.eq(memberId),
                        challengeTime.challengeId.eq(challengeId),
                        challengeParticipant.status.eq(Status.ONGOING)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Optional<ChallengeParticipant> findParticipantByMemberIdAndChallengeTimeId(Long memberId, Long challengeTimeId) {
        ChallengeParticipant result = queryFactory
                .select(challengeParticipant)
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId))
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(
                        challengeParticipant.memberId.eq(memberId),
                        challengeTime.id.eq(challengeTimeId)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Long> findIdsByChallengePassCond(
            ChallengePassCond challengePassCond,
            List<Long> participantIds,
            LocalDateTime now
    ) {

        LocalDateTime yesterdayStart = now.toLocalDate().minusDays(1).atStartOfDay();
        LocalDateTime yesterdayEnd = now.toLocalDate().atStartOfDay();

        List<Long> passedParticipantIds = queryFactory
                .select(challengeParticipant.id)
                .from(challengeParticipant)
                .join(transaction).on(transaction.memberId.eq(challengeParticipant.memberId))
                .join(category).on(category.id.eq(transaction.categoryId))
                .where(
                        challengeParticipant.memberId.in(participantIds),
                        transaction.createdAt.between(yesterdayStart, yesterdayEnd),
                        transaction.transactionType.eq(TransactionType.SPEND),
                        eqCategory(challengePassCond) // 특정 카테고리 분류
                )
                .groupBy(challengeParticipant.id)
                .having(
                        loeAmount(challengePassCond), // 금액 제한
                        loeLimitCount(challengePassCond) // 횟수 제한
                )
                .fetch();

        return passedParticipantIds;
    }

    @Override
    public ChallengeInfo findChallengeInfoByParticipantId(Long challengeParticipantId) {

        return queryFactory
                .select(
                        Projections.fields(
                                ChallengeInfo.class,
                                challenge.id.as("challengeId"),
                                challengeTime.id.as("challengeTimeId"),
                                challenge.title,
                                challenge.period,
                                challengeTime.startTime,
                                challengeTime.endTime
                        )
                )
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId))
                .join(challenge).on(challenge.id.eq(challengeTime.challengeId))
                .where(challengeParticipant.id.eq(challengeParticipantId))
                .fetchOne();
    }

    @Override
    public List<ChallengeParticipant> findSuccessParticipantsByMemberId(Long memberId) {

        return queryFactory
                .select(challengeParticipant)
                .from(challengeParticipant)
                .join(challengeTime).on(challengeTime.id.eq(challengeParticipant.challengeTimeId))
                .where(
                        challengeParticipant.memberId.eq(memberId),
                        challengeParticipant.status.eq(SUCCESS)
                )
                .orderBy(challengeTime.endTime.desc()) // 가장 최근 끝난 순으로
                .fetch();
    }

    private BooleanExpression eqCategory(ChallengePassCond challengePassCond) {
        if(Objects.isNull(challengePassCond.categoryName())) {
            return null;
        }

        return category.categoryName.eq(challengePassCond.categoryName());
    }

    private BooleanExpression loeLimitCount(ChallengePassCond challengePassCond) {
        if(Objects.isNull(challengePassCond.limitCount())) {
            return null;
        }

        return transaction.count().loe(challengePassCond.limitCount());
    }

    private BooleanExpression loeAmount(ChallengePassCond challengePassCond) {
        if(Objects.isNull(challengePassCond.amount())) {
            return null;
        }

        return transaction.balance.sum().loe(challengePassCond.amount());
    }

    private BooleanExpression nextCond(ChallengeSearchRequest request) {

        // 오픈 시간 오름차순
        if(request.lastStartTime() != null && request.next() != null) {
            return challengeTime.startTime.goe(request.lastStartTime())
                    .and(challengeTime.id.loe(request.next()));
        }

        if(request.next() != null) {
            return challengeParticipant.id.loe(request.next());
        }

        return null;
    }

    private BooleanExpression eqStatus(Status status) {
        if(Objects.isNull(status))
            return null;

        return challengeParticipant.status.eq(status);
    }

    private BooleanExpression containTag(String tag) {
        if(Objects.isNull(tag))
            return null;

        return challenge.tags.contains(tag);
    }

    private BooleanExpression containKeyword(String keyword) {
        if(Objects.isNull(keyword))
            return null;

        return challenge.title.contains(keyword);
    }

}
