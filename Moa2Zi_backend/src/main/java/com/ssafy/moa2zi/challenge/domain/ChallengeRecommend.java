package com.ssafy.moa2zi.challenge.domain;

import com.ssafy.moa2zi.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "challenge_recommends")
public class ChallengeRecommend extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_recommend_id")
    private Long id;

    @Column(nullable = false)
    private Long challengeTimeId;

    @Column(nullable = false)
    private Long memberId;

    private ChallengeRecommend(
            Long challengeTimeId,
            Long memberId
    ) {

        this.challengeTimeId = challengeTimeId;
        this.memberId = memberId;
    }

    public static ChallengeRecommend createChallengeRecommendOf(
            Long challengeTimeId,
            Long memberId
    ) {

        return new ChallengeRecommend(
                challengeTimeId,
                memberId
        );
    }

}
