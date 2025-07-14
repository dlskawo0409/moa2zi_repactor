package com.ssafy.moa2zi.category.dto.response;

import com.ssafy.moa2zi.category.domain.CategoryType;

public record CategoryInfo(
        Long categoryId,
        Long parentId,
        CategoryType categoryType,
        String categoryName
) { }
