package com.ssafy.moa2zi.challenge.domain;

import com.ssafy.moa2zi.challenge.infrastructure.GptChallengeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "challenges")
public class Challenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    private String categoryName;

    @Column(nullable = false)
    private int challengeType;

    private Integer period;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Unit unit; // 기간 단위

    private Long amount;

    private Integer limitCount;

    private Integer percent;

    private String tags; // 태그 리스트

    @Column(nullable = false)
    @ColumnDefault(value = "0")
    private int participantCount;

    private Challenge(
            String title,
            String categoryName,
            int challengeType,
            Integer period,
            Unit unit,
            Long amount,
            Integer limitCount,
            Integer percent,
            String tags
    ) {
        this.title = title;
        this.categoryName = categoryName;
        this.challengeType = challengeType;
        this.period = period;
        this.unit = unit;
        this.amount = amount;
        this.limitCount = limitCount;
        this.percent = percent;
        this.tags = tags;
    }

    public static Challenge createChallenge(
            GptChallengeInfo challengeInfo
    ) {

        return new Challenge(
                challengeInfo.challengeTitle(),
                challengeInfo.category(),
                challengeInfo.challengeType(),
                challengeInfo.period(),
                Unit.ALL,
                challengeInfo.amount(),
                challengeInfo.limitCount(),
                challengeInfo.percent(),
                formatTags(challengeInfo.tags())
        );
    }

    // 특수문자 제거 후 콤마로만 조합 (태그 포맷)
    public static String formatTags(String tags) {
        return Arrays.stream(tags.split("[^가-힣a-zA-Z0-9]+"))
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(","));
    }

}
