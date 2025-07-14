package com.ssafy.moa2zi.challenge.dto.response;

import com.ssafy.moa2zi.challenge.domain.Status;
import com.ssafy.moa2zi.challenge.domain.Unit;

import java.time.Duration;
import java.time.LocalDateTime;

public record ChallengeInfoResponse(
        Long challengeId,
        Long challengeTimeId,
        Long challengeParticipantId,
        String descriptionMessage,
        Unit unit,
        String tags,
        String categoryName,
        LocalDateTime startTime,
        LocalDateTime endTime,
        int participantCount,
        Status status,
        Integer progress
) {

    public static ChallengeInfoResponse from(ChallengeInfo challengeInfo) {

        return new ChallengeInfoResponse(
                challengeInfo.getChallengeId(),
                challengeInfo.getChallengeTimeId(),
                challengeInfo.getChallengeParticipantId(),
                challengeInfo.getTitle(),
                challengeInfo.getUnit(),
                challengeInfo.getTags(),
                challengeInfo.getCategoryName(),
                challengeInfo.getStartTime(),
                challengeInfo.getEndTime(),
                challengeInfo.getParticipantCount(),
                challengeInfo.getStatus(),
                calcProgress(challengeInfo)
        );
    }


    private static Integer calcProgress(ChallengeInfo challengeInfo) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(challengeInfo.getStartTime()))
            return 0;
        if (now.isAfter(challengeInfo.getEndTime()))
            return 100;

        Duration total = Duration.between(challengeInfo.getStartTime(), challengeInfo.getEndTime());
        Duration elapsed = Duration.between(challengeInfo.getStartTime(), now);
        double progress = (double) elapsed.toMillis() / total.toMillis() * 100;
        return (int) progress;
    }

}
