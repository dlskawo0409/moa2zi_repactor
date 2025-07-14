package com.ssafy.moa2zi.challenge.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChallengeReviewCreateRequest(
        @NotBlank
        String review
) {
}
