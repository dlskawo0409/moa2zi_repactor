package com.ssafy.moa2zi.challenge.domain;

import org.springframework.cglib.core.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeTimeRepository extends JpaRepository<ChallengeTime, Long> {
    List<ChallengeTime> findByStartTimeIsBeforeAndEndTimeIsAfter(LocalDateTime startTime, LocalDateTime endTime);

    List<ChallengeTime> findByEndTimeIsBetween(LocalDateTime startTime, LocalDateTime endTime);

}
