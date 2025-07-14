package com.ssafy.moa2zi.challenge.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "challenge_review_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"challenge_participant_id", "member_id"})
        }
)
public class ChallengeReviewLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_review_like_id")
    private Long id;

    @Column(nullable = false)
    private Long challengeParticipantId;

    @Column(nullable = false)
    private Long memberId;

    private ChallengeReviewLike(
            Long challengeParticipantId,
            Long memberId
    ) {
        this.challengeParticipantId = challengeParticipantId;
        this.memberId = memberId;
    }

    public static ChallengeReviewLike of(
            Long challengeParticipantId,
            Long memberId
    ) {

        return new ChallengeReviewLike(challengeParticipantId, memberId);
    }

}
