package com.ssafy.moa2zi.challenge.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChallengeRecommendRepository extends JpaRepository<ChallengeRecommend, Long>, ChallengeRecommendJdbcRepository {

    void deleteAllByCreatedAtBefore(LocalDateTime dateTime);
}
