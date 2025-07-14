package com.ssafy.moa2zi.challenge.dto.response;

import com.ssafy.moa2zi.challenge.domain.Challenge;
import com.ssafy.moa2zi.challenge.domain.ChallengeTime;
import com.ssafy.moa2zi.member.domain.Member;
import lombok.Builder;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

import static com.ssafy.moa2zi.challenge.dto.response.AwardType.EXISTING;
import static com.ssafy.moa2zi.challenge.dto.response.AwardType.NEW;

@Builder
public record AwardGetResponse(
        List<AwardInfoResponse> awards,
        Long total,
        Long next,
        int size,
        Boolean hasNext
) {
}
