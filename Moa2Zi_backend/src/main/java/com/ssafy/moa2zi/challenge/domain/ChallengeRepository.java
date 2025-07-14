package com.ssafy.moa2zi.challenge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long>, ChallengeRepositoryCustom {

    @Query("""
        SELECT c
        FROM Challenge c
        WHERE c.id in :challengeIds
    """)
    List<Challenge> findChallengesInChallengeIds(@Param("challengeIds") List<Long> challengeIds);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Challenge c SET c.participantCount = c.participantCount + 1 " +
            "WHERE c.id = :challengeId")
    void updateParticipantCount(@Param("challengeId") Long challengeId);
}
