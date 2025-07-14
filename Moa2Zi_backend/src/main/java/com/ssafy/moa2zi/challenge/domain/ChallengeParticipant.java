package com.ssafy.moa2zi.challenge.domain;

import com.ssafy.moa2zi.challenge.dto.request.ChallengeReviewCreateRequest;
import com.ssafy.moa2zi.member.domain.Gender;
import com.ssafy.moa2zi.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "challenge_participants",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"challenge_time_id", "member_id"})
        }
)
public class ChallengeParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_participant_id")
    private Long id;

    @Column(nullable = false)
    private Long challengeTimeId;

    @Column(nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String review;

    private LocalDateTime reviewedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    @ColumnDefault(value = "true")
    private Boolean checkRequired; // 챌린지 상태 체크 대상 여부 (도중 실패한 경우 false)

    @Column(length = 1000)
    private String awardImage;

    private Boolean isRead;

    private ChallengeParticipant(
            Long challengeTimeId,
            Long memberId,
            Gender gender,
            int age
    ) {
        this.challengeTimeId = challengeTimeId;
        this.memberId = memberId;
        this.status = Status.ONGOING;
        this.gender = gender;
        this.age = age;
        this.checkRequired = true;
        this.isRead = false;
    }

    public static ChallengeParticipant createWithChallengeTimeAndMember(
            Long challengeTimeId,
            Member member
    ) {

        return new ChallengeParticipant(
                challengeTimeId,
                member.getMemberId(),
                member.getGender(),
                ((LocalDate.now().getYear() - member.getBirthday().getYear() + 1) / 10) * 10
        );
    }

    public void createReview(ChallengeReviewCreateRequest request) {
        this.review = request.review();
        this.reviewedAt = LocalDateTime.now();
    }

    public void updateAwardImage(String awardImage) {
        this.awardImage = awardImage;
    }

    public void markAsRead() {
        this.isRead = true;
    }

}
