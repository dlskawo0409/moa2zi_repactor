package com.ssafy.moa2zi.lounge.dto.request;

import com.ssafy.moa2zi.lounge.domain.LoungeStatus;
import com.ssafy.moa2zi.lounge.domain.SearchType;
import jakarta.validation.constraints.NotNull;

public record LoungeGetRequest(
        @NotNull
        SearchType searchType,
        String keyword,
        Long next,
        LoungeStatus loungeStatus,
        Integer size
) {
}
