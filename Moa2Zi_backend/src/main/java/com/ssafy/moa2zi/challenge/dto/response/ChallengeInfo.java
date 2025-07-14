package com.ssafy.moa2zi.challenge.dto.response;

import com.ssafy.moa2zi.challenge.domain.Status;
import com.ssafy.moa2zi.challenge.domain.Unit;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ChallengeInfo {
    Long challengeId;
    Long challengeTimeId;
    Long challengeParticipantId;
    String title;
    Integer period;
    Unit unit;
    int challengeType;
    Long amount;
    Integer limitCount;
    Integer percent;
    String tags;
    String categoryName;
    LocalDateTime startTime;
    LocalDateTime endTime;
    int participantCount;
    Status status;

    public Long getAmount() {
        return (amount != null) ? amount : 0;
    }

    public Integer getLimitCount() {
        return (limitCount != null) ? limitCount : 0;
    }

    public Integer getPercent() {
        return (percent != null) ? percent : 0;
    }

}
