package com.ssafy.moa2zi.challenge.dto.response;

import java.time.LocalDateTime;

public record ReviewInfoResponse(
        ParticipantInfoResponse participantInfo,
        String review,
        Long reviewLikeCount,
        boolean isLikedByMe,
        LocalDateTime reviewedAt
) {

    public static ReviewInfoResponse from(
            ReviewInfo reviewInfo,
            ParticipantInfoResponse participantInfo,
            Long reviewLikeCount,
            boolean isLikedByMe
    ) {

        return new ReviewInfoResponse(
                participantInfo,
                reviewInfo.review(),
                reviewLikeCount,
                isLikedByMe,
                reviewInfo.reviewedAt()
        );
    }

}
