package com.ssafy.moa2zi.challenge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeReviewLikeRepository extends JpaRepository<ChallengeReviewLike, Long> {
    boolean existsByMemberIdAndChallengeParticipantId(Long memberId, Long challengeParticipantId);
    void deleteByMemberIdAndChallengeParticipantId(Long memberId, Long challengeParticipantId);
}
