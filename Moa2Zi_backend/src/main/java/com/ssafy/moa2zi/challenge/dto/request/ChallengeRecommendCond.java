package com.ssafy.moa2zi.challenge.dto.request;

import com.ssafy.moa2zi.member.domain.Gender;
import lombok.Builder;

import java.util.List;

@Builder
public record ChallengeRecommendCond(
        List<String> topSpendingCategoryList,
        Gender gender,
        Integer age,
        boolean excludePrevious
) {
}
