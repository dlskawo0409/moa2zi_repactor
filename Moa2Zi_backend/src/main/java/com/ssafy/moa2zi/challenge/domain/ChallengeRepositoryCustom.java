package com.ssafy.moa2zi.challenge.domain;

import com.ssafy.moa2zi.challenge.dto.request.*;
import com.ssafy.moa2zi.challenge.dto.request.ChallengePassCond;
import com.ssafy.moa2zi.challenge.dto.request.ChallengeRecommendCond;
import com.ssafy.moa2zi.challenge.dto.request.ChallengeSearchRequest;
import com.ssafy.moa2zi.challenge.dto.request.ParticipantGetRequest;
import com.ssafy.moa2zi.challenge.dto.response.*;
import com.ssafy.moa2zi.member.dto.request.CustomMemberDetails;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChallengeRepositoryCustom {

    ChallengeSearchResponse findChallengesByMember(
            ChallengeSearchRequest request,
            CustomMemberDetails loginMember
    );

    ChallengeSearchResponse findChallenges(
            ChallengeSearchRequest request,
            CustomMemberDetails loginMember
    );

    ChallengeSearchResponse findPopularChallenges(int topN);

    ParticipantGetResponse findParticipants(
            Long challengeId,
            ParticipantGetRequest request,
            CustomMemberDetails loginMember
    );

    ReviewGetResponse findReviews(
            Long challengeId,
            ReviewGetRequest request,
            CustomMemberDetails loginMember
    );

    List<Long> findTopNChallengesByCond(
            Long memberId,
            ChallengeRecommendCond challengeRecommendCond,
            int topN,
            Set<Long> excludeIds
    );

    ChallengeSearchResponse findRecommendChallengesByMember(
            CustomMemberDetails loginMember,
            int topN
    );

    boolean existsOngoingByMemberIdAndChallengeId(Long memberId, Long challengeId);

    Optional<ChallengeParticipant> findParticipantByMemberIdAndChallengeTimeId(Long memberId, Long challengeTimeId);

    List<Long> findIdsByChallengePassCond(
            ChallengePassCond challengePassCond,
            List<Long> participantIds,
            LocalDateTime now
    );

    ChallengeInfo findChallengeInfoByParticipantId(Long challengeParticipantId);

    List<ChallengeParticipant> findSuccessParticipantsByMemberId(Long memberId);

}
