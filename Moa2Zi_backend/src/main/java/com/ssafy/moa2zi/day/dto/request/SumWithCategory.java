package com.ssafy.moa2zi.day.dto.request;

import lombok.Builder;

@Builder
public record SumWithCategory(
        Long sum,
        String categoryName
) {
}
