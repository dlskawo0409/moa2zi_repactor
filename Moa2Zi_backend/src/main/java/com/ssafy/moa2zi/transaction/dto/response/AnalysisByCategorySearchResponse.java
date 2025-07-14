package com.ssafy.moa2zi.transaction.dto.response;

public record AnalysisByCategorySearchResponse(
        Long categoryId,
        Long parentId,
        String categoryName,
        Integer percent,
        Long sum
) {
}
