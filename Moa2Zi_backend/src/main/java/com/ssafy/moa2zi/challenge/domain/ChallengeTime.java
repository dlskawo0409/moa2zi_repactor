package com.ssafy.moa2zi.challenge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "challenge_times")
public class ChallengeTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_time_id")
    private Long id;

    @Column(nullable = false)
    private Long challengeId;

    @Column(nullable = false)
    private LocalDateTime startTime; // 오픈시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 종료시간

    private ChallengeTime(
            Long challengeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        this.challengeId = challengeId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static ChallengeTime openChallengeTimeOf(
            Long challengeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {

        return new ChallengeTime(
                challengeId,
                startTime,
                endTime
        );
    }

}
