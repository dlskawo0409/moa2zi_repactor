package com.ssafy.moa2zi.lounge.dto.response;

import com.ssafy.moa2zi.lounge.domain.LoungeStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record LoungeListResponse(
        List<LoungeSearchResponse> loungeList,
		Long unReadNumSum,
        Long total,
        Integer size,
        Boolean hasNext,
        Long next,
		LoungeStatus loungeStatus
) {
}
