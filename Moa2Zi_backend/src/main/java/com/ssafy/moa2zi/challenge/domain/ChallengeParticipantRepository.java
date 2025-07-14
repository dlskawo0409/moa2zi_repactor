package com.ssafy.moa2zi.challenge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {

    List<ChallengeParticipant> findByChallengeTimeIdAndCheckRequiredTrue(Long challengeTimeId);

    List<ChallengeParticipant> findByChallengeTimeId(Long challengeTimeId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE ChallengeParticipant cp
        SET cp.checkRequired = false
        WHERE cp.id IN :participantIds
    """)
    void updateCheckRequiredFalseByIds(@Param("participantIds") List<Long> participantIds);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE ChallengeParticipant cp SET cp.status = :status WHERE cp.id IN :participantIds")
    void updateStatusByIds(@Param("status") Status status, @Param("participantIds") List<Long> participantIds);

    Long countByMemberIdAndStatus(Long memberId, Status status);
}
